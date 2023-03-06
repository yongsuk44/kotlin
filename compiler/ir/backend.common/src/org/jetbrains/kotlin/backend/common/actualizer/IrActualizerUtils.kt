/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.actualizer

import org.jetbrains.kotlin.KtDiagnosticReporterWithImplicitIrBasedContext
import org.jetbrains.kotlin.backend.common.CommonBackendErrors
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.module
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.multiplatform.OptionalAnnotationUtil

fun generateIrElementFullName(
    declaration: IrElement,
    expectActualTypesMap: Map<IrSymbol, IrSymbol>? = null,
    typeAliasMap: Map<FqName, FqName>? = null
): String {
    return StringBuilder().apply { appendElementFullName(declaration, this, expectActualTypesMap, typeAliasMap) }.toString()
}

private fun appendElementFullName(
    declaration: IrElement,
    result: StringBuilder,
    expectActualTypesMap: Map<IrSymbol, IrSymbol>? = null,
    expectActualTypeAliasMap: Map<FqName, FqName>? = null
) {
    if (declaration !is IrDeclarationBase) return

    val parents = mutableListOf<String>()
    var parent: IrDeclarationParent? = declaration.parent
    while (parent != null) {
        if (parent is IrDeclarationWithName) {
            val parentParent = parent.parent
            if (parentParent is IrClass) {
                parents.add(parent.name.asString())
                parent = parentParent
                continue
            }
        }
        val parentString = parent.kotlinFqName.let { (expectActualTypeAliasMap?.get(it) ?: it).asString() }
        if (parentString.isNotEmpty()) {
            parents.add(parentString)
        }
        parent = null
    }

    if (parents.isNotEmpty()) {
        result.append(parents.asReversed().joinToString(separator = "."))
        result.append('.')
    }

    if (declaration is IrDeclarationWithName) {
        result.append(declaration.name)
    }

    if (declaration is IrFunction) {
        fun appendType(type: IrType) {
            val typeClassifier = type.classifierOrFail
            val actualizedTypeSymbol = expectActualTypesMap?.get(typeClassifier) ?: typeClassifier
            appendElementFullName(actualizedTypeSymbol.owner, result, expectActualTypesMap)
        }

        val extensionReceiverType = declaration.extensionReceiverParameter?.type
        if (extensionReceiverType != null) {
            result.append('[')
            appendType(extensionReceiverType)
            result.append(']')
        }

        result.append('(')
        for ((index, parameter) in declaration.valueParameters.withIndex()) {
            appendType(parameter.type)
            if (index < declaration.valueParameters.size - 1) {
                result.append(',')
            }
        }
        result.append(')')
    }
}

@OptIn(ObsoleteDescriptorBasedAPI::class)
fun KtDiagnosticReporterWithImplicitIrBasedContext.reportMissingActual(irDeclaration: IrDeclaration) {
    at(irDeclaration).report(CommonBackendErrors.NO_ACTUAL_FOR_EXPECT, irDeclaration.module)
}

fun KtDiagnosticReporterWithImplicitIrBasedContext.reportManyInterfacesMembersNotImplemented(declaration: IrClass, actualMember: IrDeclarationWithName) {
    at(declaration).report(CommonBackendErrors.MANY_INTERFACES_MEMBER_NOT_IMPLEMENTED, actualMember.name.asString())
}

internal fun IrElement.containsOptionalExpectation(): Boolean {
    return this is IrClass &&
            this.kind == ClassKind.ANNOTATION_CLASS &&
            this.hasAnnotation(OptionalAnnotationUtil.OPTIONAL_EXPECTATION_FQ_NAME)
}