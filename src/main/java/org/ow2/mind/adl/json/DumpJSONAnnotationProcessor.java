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

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.AbstractADLLoaderAnnotationProcessor;
import org.ow2.mind.adl.ast.ASTHelper;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.annotation.Annotation;

/**
 * Inspired by Matthieu Anne's DumpAST
 */
public class DumpJSONAnnotationProcessor
extends
AbstractADLLoaderAnnotationProcessor {

	private static void showComponents(final Definition definition,
			final int depth) {
		String prf = "  ";

		for (int i = 0; i < depth; i++)
			prf = prf + "  ";

		if (ASTHelper.isComposite(definition)) {
			
			// End line with ",\n"
			System.out.println(",");
			
			final Component[] subComponents = ((ComponentContainer) definition)
					.getComponents();

			if (subComponents.length > 0)
				System.out.println(prf + "\"children\": [");

			for (int i = 0; i < subComponents.length; i++) {
				final Component subComponent = subComponents[i];
				try {
					Definition subComponentDef = ASTHelper.getResolvedDefinition(
							subComponent.getDefinitionReference(), null, null);

					System.out.print(prf + " {\n"
							+ prf + prf + "\"name\": " + "\""	+ /*subComponentDef.getName()	+ " " + */ subComponent.getName() + "\"");

					if (subComponentDef != null)
						showComponents(subComponentDef, depth + 1);

					// Line return and close the current child
					System.out.println();
					System.out.print(prf + " }");

					// add "," to all entries except the last one
					if (i < subComponents.length-1)
						System.out.print(",");

					// \n in any case
					System.out.println();
					
				} catch (final ADLException e) {
					e.printStackTrace();
				}
			}

			if (subComponents.length > 0)
				System.out.println(prf + "]");
		}
	}

	public static void showDefinitionContent(final Definition definition) {
		System.out.println("{");
		System.out.print("  \"name\": " + "\"" + definition.getName() + "\"");

		showComponents(definition, 0);

		System.out.println("}");

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
		showDefinitionContent(definition);
		return null;
	}

}
