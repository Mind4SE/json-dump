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
 * 
 * Inspired by Matthieu Anne's DumpAST
 */

package org.ow2.mind.adl.json;

import org.ow2.mind.adl.annotation.ADLAnnotationTarget;
import org.ow2.mind.adl.annotation.ADLLoaderPhase;
import org.ow2.mind.adl.annotation.ADLLoaderProcessor;
import org.ow2.mind.annotation.Annotation;
import org.ow2.mind.annotation.AnnotationTarget;

/**
 * @author Stephane Seyvoz
 */
@ADLLoaderProcessor(processor = DumpJSONAnnotationProcessor.class, phases = { ADLLoaderPhase.AFTER_CHECKING })
public class DumpJSON implements Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1195656706804274444L;
	private static final AnnotationTarget[] ANNOTATION_TARGETS = { ADLAnnotationTarget.DEFINITION };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.mind.annotation.Annotation#getAnnotationTargets()
	 */
	public AnnotationTarget[] getAnnotationTargets() {
		return ANNOTATION_TARGETS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ow2.mind.annotation.Annotation#isInherited()
	 */
	public boolean isInherited() {
		return true;
	}

}
