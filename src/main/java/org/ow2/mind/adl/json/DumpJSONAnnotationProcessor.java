/**
 * Copyright (C) 2014 Schneider-Electric
 *
 * This file is part of "Mind Compiler" is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Stephane Seyvoz
 * Contributors:
 */

package org.ow2.mind.adl.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.ow2.mind.NameHelper;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.compilation.CompilerContextHelper;
import org.ow2.mind.io.OutputFileLocator;

import com.google.inject.Inject;

/**
 * Inspired by Matthieu Anne's DumpAST
 */
public class DumpJSONAnnotationProcessor
extends
AbstractADLLoaderAnnotationProcessor {

	@Inject
	OutputFileLocator outputFileLocatorItf;
	
	protected File outFile;
	protected PrintWriter printWriter;
	protected static String extension = ".json";

	private void showComponents(final Definition definition,
			final int depth) {
		String prf = "  ";

		for (int i = 0; i < depth; i++)
			prf = prf + "  ";

		if (ASTHelper.isComposite(definition)) {

			// End line with ",\n"
			printWriter.println(",");

			final Component[] subComponents = ((ComponentContainer) definition)
					.getComponents();

			if (subComponents.length > 0)
				printWriter.println(prf + "\"children\": [");

			for (int i = 0; i < subComponents.length; i++) {
				final Component subComponent = subComponents[i];
				try {
					Definition subComponentDef = ASTHelper.getResolvedDefinition(
							subComponent.getDefinitionReference(), null, null);

					printWriter.print(prf + " {\n"
							+ prf + prf + "\"name\": " + "\""	+ /*subComponentDef.getName()	+ " " + */ subComponent.getName() + "\"");

					if (subComponentDef != null)
						showComponents(subComponentDef, depth + 1);

					// Line return and close the current child
					printWriter.println();
					printWriter.print(prf + " }");

					// add "," to all entries except the last one
					if (i < subComponents.length-1)
						printWriter.print(",");

					// \n in any case
					printWriter.println();

				} catch (final ADLException e) {
					e.printStackTrace();
				}
			}

			if (subComponents.length > 0)
				printWriter.println(prf + "]");
		}
	}

	public void showDefinitionContent(final Definition definition) {
		printWriter.println("{");
		printWriter.print("  \"name\": " + "\"" + definition.getName() + "\"");

		showComponents(definition, 0);

		printWriter.println("}");

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.ow2.mind.adl.annotation.ADLLoaderAnnotationProcessor#processAnnotation
	 * (org.ow2.mind.annotation.Annotation, org.objectweb.fractal.adl.Node,
	 * org.objectweb.fractal.adl.Definition,
	 * org.ow2.mind.adl.annotation.ADLLoaderPhase, java.util.Map)
	 */
	public Definition processAnnotation(final Annotation annotation,
			final Node node, final Definition definition, final ADLLoaderPhase phase,
			final Map<Object, Object> context) throws ADLException {
		
		assert annotation instanceof DumpJSON;

		String nameInOutput = null;
		
		// Specify an output name ?
		String executableName = CompilerContextHelper.getExecutableName(context);
		if (executableName != null)
			nameInOutput = executableName;
		else
			nameInOutput = NameHelper.toValidName(definition.getName()).replace('.', '_');
		
		nameInOutput = "/" + nameInOutput + extension;
		
		// could also use getCSourceOutputFile
		this.outFile = outputFileLocatorItf.getMetadataOutputFile(nameInOutput, context);

		try {
			printWriter = new PrintWriter( new FileWriter( this.outFile.getAbsolutePath() ) );
			showDefinitionContent(definition);
			printWriter.close();
		} catch (IOException e) {
			/* 
			 * "if the named file exists but is a directory rather than a regular file, does not exist but cannot be created,
			 * or cannot be opened for any other reason"
			 * */
			logger.warning("Could not generate JSON for definition '" + definition.getName() + "' !");
			logger.warning("Error was: " + e.getMessage());
		}


		return null;
	}

}
