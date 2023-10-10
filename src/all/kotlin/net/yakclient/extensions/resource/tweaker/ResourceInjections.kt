package net.yakclient.extensions.resource.tweaker

import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.resources.FallbackResourceManager
import net.yakclient.client.api.BEFORE_END
import net.yakclient.client.api.annotation.Mixin
import net.yakclient.client.api.annotation.SourceInjection
import java.util.*
import kotlin.collections.HashMap


@Mixin("net.minecraft.server.packs.resources.MultiPackResourceManager")
abstract class ResourceInjections {
    private val namespacedManagers: MutableMap<String, FallbackResourceManager> = HashMap()

    @SourceInjection(
        point = BEFORE_END,
        from = "net.yakclient.extensions.resource.tweaker.ResourceInjections",
        to = "net.minecraft.server.packs.resources.MultiPackResourceManager",
        methodFrom = "pushPack()V",
        methodTo = "<init>(Lnet/minecraft/server/packs/PackType;Ljava/util/List;)V"
    )
    fun pushPack() {
        ResourceEnvironmentTweaker.extensions.flatMap { ref ->
            val pack = ArchiveReferencePackResources(UUID.randomUUID().toString(), ref, false)

            pack.getNamespaces(PackType.CLIENT_RESOURCES).map { it to pack }
        }.map { (namespace, resources) ->
            val pack = FallbackResourceManager(PackType.CLIENT_RESOURCES, namespace)

            pack.push(resources)

            pack
        }.forEach {
            namespacedManagers[UUID.randomUUID().toString()] = it
        }
    }
}