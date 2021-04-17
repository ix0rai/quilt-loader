/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.loader.impl.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Changes package-private and protected access flags to public.
 * In a development environment, Minecraft classes may be mapped into a package structure with invalid access across
 * packages. The class verifier will complain unless we simply change package-private and protected to public.
 */
public class PackageAccessFixer extends ClassVisitor {
	private static int modAccess(int access) {
		if ((access & 0x7) != Opcodes.ACC_PRIVATE) {
			return (access & (~0x7)) | Opcodes.ACC_PUBLIC;
		} else {
			return access;
		}
	}

	public PackageAccessFixer(int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	@Override
	public void visit(
		final int version,
		final int access,
		final String name,
		final String signature,
		final String superName,
		final String[] interfaces) {
		super.visit(version, modAccess(access), name, signature, superName, interfaces);
	}

	@Override
	public void visitInnerClass(
		final String name, final String outerName, final String innerName, final int access) {
		super.visitInnerClass(name, outerName, innerName, modAccess(access));
	}

	@Override
	public FieldVisitor visitField(
		final int access,
		final String name,
		final String descriptor,
		final String signature,
		final Object value) {
		return super.visitField(modAccess(access), name, descriptor, signature, value);
	}

	@Override
	public MethodVisitor visitMethod(
		final int access,
		final String name,
		final String descriptor,
		final String signature,
		final String[] exceptions) {
		return super.visitMethod(modAccess(access), name, descriptor, signature, exceptions);
	}
}
