package deplatformed.processing

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ksp.toClassName

class PlatformAnnotationProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    companion object {
        val platformImplName = "deplatformed.ServiceImpl"
    }

    val serviceImplementors: Multimap<String, Pair<String, KSFile>> = HashMultimap.create()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val deferred = mutableListOf<KSAnnotated>()

        val platformImplType =
            resolver.getClassDeclarationByName(resolver.getKSNameFromString(platformImplName))
                ?.asType(emptyList())
                ?: run {
                    val message = "@PlatformImpl type not found on the classpath, skipping processing."
                    logger.info(message)
                    return emptyList()
                }

        resolver.getSymbolsWithAnnotation(platformImplName)
            .filterIsInstance<KSClassDeclaration>()
            .forEach { serviceImplementor ->
                val annotation = serviceImplementor.annotations.find { it.annotationType.resolve() == platformImplType }
                    ?: run {
                        logger.error("@PlatformImpl annotation not found", serviceImplementor)
                        return@forEach
                    }

                val argumentValue = annotation.arguments.find { it.name?.getShortName() == "value" }!!.value

                val services =
                    try {
                        argumentValue as? List<KSType> ?: listOf(argumentValue as KSType)
                    } catch (ignored: ClassCastException) {
                        logger.error("No `value` member member found.", annotation)
                        return@forEach
                    }

                if (services.isEmpty()) {
                    logger.info("No service implementations found", serviceImplementor)
                }

                services.forEach services@{ service ->
                    if (service.isError) {
                        deferred.add(serviceImplementor)
                        return@forEach
                    }

                    val serviceClassDeclaration = service.declaration.closestClassDeclaration()!!
                    when (checkImplementer(serviceImplementor, service)) {
                        ValidationResult.VALID -> {
                            serviceImplementors.put(
                                serviceClassDeclaration.toBinaryName(),
                                serviceImplementor.toBinaryName() to serviceImplementor.containingFile!!
                            )
                        }

                        ValidationResult.INVALID -> {
                            logger.error("Service implementation must extend the service", serviceImplementor)
                        }

                        ValidationResult.DEFERRED -> {
                            deferred += serviceImplementor
                        }
                    }
                }
            }

        serviceImplementors.keys().forEach { service ->
            val resourceFile = "META-INF/services/$service"
            val implementors = serviceImplementors[service]
            val implementorNames = implementors.map { it.first }.toSet()
            val files = implementors.map { it.second }
            val dependencies = Dependencies(true, *files.toTypedArray())
            codeGenerator.createNewFile(dependencies, "", resourceFile, "").bufferedWriter().use { writer ->
                implementorNames.forEach { implementor ->
                    writer.write(implementor)
                    writer.newLine()
                }
            }
        }

        serviceImplementors.clear()

        return deferred
    }

    fun KSClassDeclaration.toBinaryName(): String {
        return toClassName().reflectionName()
    }

    fun checkImplementer(
        implementer: KSClassDeclaration,
        serviceType: KSType
    ): ValidationResult {
        implementer.getAllSuperTypes().forEach { superType ->
            if (superType.isAssignableFrom(serviceType)) {
                return ValidationResult.VALID
            } else if (serviceType.isError) {
                return ValidationResult.DEFERRED
            }
        }

        return ValidationResult.INVALID
    }

    enum class ValidationResult {
        VALID,
        INVALID,
        DEFERRED
    }
}
