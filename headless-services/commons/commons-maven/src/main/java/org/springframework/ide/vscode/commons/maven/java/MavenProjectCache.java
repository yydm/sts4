/*******************************************************************************
 * Copyright (c) 2016, 2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.vscode.commons.maven.java;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.springframework.ide.vscode.commons.languageserver.Sts4LanguageServer;
import org.springframework.ide.vscode.commons.languageserver.java.AbstractFileToProjectCache;
import org.springframework.ide.vscode.commons.languageserver.util.ShowMessageException;
import org.springframework.ide.vscode.commons.maven.MavenCore;

/**
 * Cache for Maven projects
 *
 * @author Alex Boyko
 */
public class MavenProjectCache extends AbstractFileToProjectCache<MavenJavaProject> {

	private MavenCore maven;
	
	public MavenProjectCache(Sts4LanguageServer server, MavenCore maven, boolean asyncUpdate, Path projectCacheFolder) {
		super(server, asyncUpdate, projectCacheFolder);
		this.maven = maven;
	}

	@Override
	protected boolean update(MavenJavaProject project) {
		try {
			return project.update();
		} catch (Exception e) {
			server.getDiagnosticService().diagnosticEvent(new ShowMessageException(
					new MessageParams(MessageType.Error, "Cannot load Maven project model from Pom file: " + project.pom()), e));
			return false;
		}
	}

	@Override
	protected MavenJavaProject createProject(File pomFile) throws Exception {
		MavenJavaProject mavenJavaProject = new MavenJavaProject(maven, pomFile,
				projectCacheFolder == null ? null : pomFile.getParentFile().toPath().resolve(projectCacheFolder)
			);
		performUpdate(mavenJavaProject, asyncUpdate, asyncUpdate);
		return mavenJavaProject;
	}

}
