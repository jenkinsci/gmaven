package org.codehaus.groovy.maven.plugin.execute;

import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.execution.MavenSession;

import java.util.List;
import java.io.File;

/**
 * Object exposed to the Groovy script, which provides access to Maven components and utility methods.
 *
 * @author Kohsuke Kawaguchi
 */
public class MavenUtility {
    public final ArtifactResolver artifactResolver;
    public final ArtifactFactory artifactFactory;
    public final ArtifactMetadataSource artifactMetadataSource;
    public final ArtifactHandlerManager artifactHandlerManager;
    public final ArtifactRepository localRepository;
    public final List remoteRepositories;
    public final MavenProjectHelper projectHelper;
    public final MavenProjectBuilder projectBuilder;
    public final MavenProject project;
    public final MavenSession session;

    public MavenUtility(ArtifactResolver artifactResolver, ArtifactFactory artifactFactory, ArtifactMetadataSource artifactMetadataSource, ArtifactHandlerManager artifactHandlerManager, ArtifactRepository localRepository, List remoteRepositories, MavenProjectHelper projectHelper, MavenProjectBuilder projectBuilder, MavenProject project, MavenSession session) {
        this.artifactResolver = artifactResolver;
        this.artifactFactory = artifactFactory;
        this.artifactMetadataSource = artifactMetadataSource;
        this.artifactHandlerManager = artifactHandlerManager;
        this.localRepository = localRepository;
        this.remoteRepositories = remoteRepositories;
        this.projectHelper = projectHelper;
        this.projectBuilder = projectBuilder;
        this.project = project;
        this.session = session;
    }

    /**
     * Given the "groupId:artifactId:version[:classifier[:type]]" string,
     * resolves an artifact and returns its location. 
     */
    public File resolveArtifact(String id) throws ArtifactResolutionException, ArtifactNotFoundException {
        String[] tokens = id.split(":");
        String classifier   = tokens.length>=4 ? tokens[3] : null;
        String type         = tokens.length>=5 ? tokens[4] : "jar";
        Artifact a = artifactFactory.createArtifactWithClassifier(tokens[0], tokens[1], tokens[2], type, classifier);
        artifactResolver.resolve(a,remoteRepositories,localRepository);
        return a.getFile();
    }

    /**
     * Attaches an artfact to the build with the given classifier.
     */
    public void attachArtifact(File artifact, String classifier) {
        projectHelper.attachArtifact(project, classifier, artifact);
    }

    /**
     * Sets the main artifact of the module.
     */
    public void attachArtifact(File artifact) {
        project.getArtifact().setFile(artifact);
    }
}
