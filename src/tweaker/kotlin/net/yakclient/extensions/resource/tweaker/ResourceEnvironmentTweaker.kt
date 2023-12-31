package net.yakclient.extensions.resource.tweaker

import net.yakclient.archives.ArchiveReference
import net.yakclient.components.extloader.api.environment.ExtLoaderEnvironment
import net.yakclient.components.extloader.api.extension.observeNodes
import net.yakclient.components.extloader.api.tweaker.EnvironmentTweaker
import net.yakclient.components.extloader.extension.ExtensionNode

class ResourceEnvironmentTweaker : EnvironmentTweaker {
        companion object {
        val internalExtensions : MutableList<ArchiveReference> = ArrayList()
        val extensions: List<ArchiveReference>
            get() = internalExtensions.toList()
    }
    override fun tweak(environment: ExtLoaderEnvironment) {
        run {
            println("idk")
        }
        environment.observeNodes {it : ExtensionNode ->
            it.archiveReference?.let(internalExtensions::add)
        }
    }
}
