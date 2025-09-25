package dev.mizarc.bellclaims.di

import dev.mizarc.bellclaims.BellClaims
import dev.mizarc.bellclaims.application.actions.claim.*
import dev.mizarc.bellclaims.application.actions.claim.anchor.BreakClaimAnchor
import dev.mizarc.bellclaims.application.actions.claim.anchor.GetClaimAnchorAtPosition
import dev.mizarc.bellclaims.application.actions.claim.anchor.MoveClaimAnchor
import dev.mizarc.bellclaims.application.actions.claim.flag.*
import dev.mizarc.bellclaims.application.actions.claim.metadata.*
import dev.mizarc.bellclaims.application.actions.claim.partition.*
import dev.mizarc.bellclaims.application.actions.claim.permission.*
import dev.mizarc.bellclaims.application.actions.claim.transfer.*
import dev.mizarc.bellclaims.application.actions.player.*
import dev.mizarc.bellclaims.application.actions.player.tool.*
import dev.mizarc.bellclaims.application.actions.player.visualisation.*
import dev.mizarc.bellclaims.application.persistence.*
import dev.mizarc.bellclaims.application.services.*
import dev.mizarc.bellclaims.application.services.scheduling.SchedulerService
import dev.mizarc.bellclaims.application.utilities.LocalizationProvider
import dev.mizarc.bellclaims.infrastructure.persistence.claims.ClaimFlagRepositorySQLite
import dev.mizarc.bellclaims.infrastructure.persistence.claims.ClaimPermissionRepositorySQLite
import dev.mizarc.bellclaims.infrastructure.persistence.claims.ClaimRepositorySQLite
import dev.mizarc.bellclaims.infrastructure.persistence.claims.PlayerAccessRepositorySQLite
import dev.mizarc.bellclaims.infrastructure.persistence.partitions.PartitionRepositorySQLite
import dev.mizarc.bellclaims.infrastructure.persistence.players.PlayerStateRepositoryMemory
import dev.mizarc.bellclaims.infrastructure.persistence.storage.SQLiteStorage
import dev.mizarc.bellclaims.infrastructure.persistence.storage.Storage
import dev.mizarc.bellclaims.infrastructure.services.*
import dev.mizarc.bellclaims.infrastructure.services.scheduling.SchedulerServiceBukkit
import dev.mizarc.bellclaims.infrastructure.utilities.LocalizationProviderProperties
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.milkbowl.vault.chat.Chat
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

// Define your Koin module(s) - using a top-level val named appModule is common
fun appModule(plugin: BellClaims) = module {
    // --- Plugin ---
    single<Plugin> {plugin}
    single<File> { plugin.dataFolder }
    single<FileConfiguration> { plugin.config }
    single<Chat> { plugin.metadata }
    single<CoroutineScope> { plugin.pluginScope }
    single<CoroutineDispatcher>(named("IODispatcher")) { Dispatchers.IO }

    // --- Config ---
    single { get<ConfigService>().loadConfig() }


    // --- Infrastructure Layer Implementations ---

    // Storage Types
    single<Storage<*>> { SQLiteStorage(get()) }
    single { SQLiteStorage(get()) }

    // Repositories
    single<ClaimFlagRepository> { ClaimFlagRepositorySQLite(get()) }
    single<ClaimPermissionRepository> { ClaimPermissionRepositorySQLite(get()) }
    single<ClaimRepository> { ClaimRepositorySQLite(get()) }
    single<PartitionRepository> { PartitionRepositorySQLite(get()) }
    single<PlayerAccessRepository> { PlayerAccessRepositorySQLite(get()) }
    single<PlayerStateRepository> { PlayerStateRepositoryMemory() }

    // Services
    single<ConfigService> { ConfigServiceBukkit(get()) }
    single<PlayerLocaleService> { PlayerLocaleServicePaper() }
    single<PlayerMetadataService> { PlayerMetadataServiceVault(get(), get()) }
    single<VisualisationService> { VisualisationServiceBukkit() }
    single<WorldManipulationService> { WorldManipulationServiceBukkit() }
    single<SchedulerService> { SchedulerServiceBukkit(get()) }
    single<ToolItemService> { ToolItemServiceBukkit(get(), get()) }

    // Utilities
    single<LocalizationProvider> { LocalizationProviderProperties(get(), get(), get()) }


    // --- Application Layer Actions ---

    // Claim
    singleOf(::CreateClaim)
    singleOf(::GetClaimAtPosition)
    singleOf(::IsNewClaimLocationValid)
    singleOf(::IsPlayerActionAllowed)
    singleOf(::IsWorldActionAllowed)
    singleOf(::ListPlayerClaims)

    // Claim / Anchor
    singleOf(::BreakClaimAnchor)
    singleOf(::GetClaimAnchorAtPosition)
    singleOf(::MoveClaimAnchor)

    // Claim / Flag
    singleOf(::DisableAllClaimFlags)
    singleOf(::DisableClaimFlag)
    singleOf(::DoesClaimHaveFlag)
    singleOf(::EnableAllClaimFlags)
    singleOf(::EnableClaimFlag)
    singleOf(::GetClaimFlags)

    // Claim / Metadata
    singleOf(::GetClaimBlockCount)
    singleOf(::GetClaimDetails)
    singleOf(::UpdateClaimDescription)
    singleOf(::UpdateClaimIcon)
    singleOf(::UpdateClaimName)

    // Claim / Partition
    singleOf(::CanRemovePartition)
    singleOf(::CreatePartition)
    singleOf(::GetClaimPartitions)
    singleOf(::GetPartitionByPosition)
    singleOf(::RemovePartition)
    singleOf(::ResizePartition)

    // Claim / Permission
    singleOf(::GetClaimPermissions)
    singleOf(::GetClaimPlayerPermissions)
    singleOf(::GetPlayersWithPermissionInClaim)
    singleOf(::GrantAllClaimWidePermissions)
    singleOf(::GrantAllPlayerClaimPermissions)
    singleOf(::GrantClaimWidePermission)
    singleOf(::GrantPlayerClaimPermission)
    singleOf(::RevokeAllClaimWidePermissions)
    singleOf(::RevokeAllPlayerClaimPermissions)
    singleOf(::RevokeClaimWidePermission)
    singleOf(::RevokePlayerClaimPermission)

    // Claim / Transfer
    singleOf(::AcceptTransferRequest)
    singleOf(::CanPlayerReceiveTransferRequest)
    singleOf(::DoesPlayerHaveTransferRequest)
    singleOf(::OfferPlayerTransferRequest)
    singleOf(::WithdrawPlayerTransferRequest)

    // Player
    singleOf(::DoesPlayerHaveClaimOverride)
    singleOf(::GetRemainingClaimBlockCount)
    singleOf(::IsPlayerInClaimMenu)
    singleOf(::RegisterClaimMenuOpening)
    singleOf(::ToggleClaimOverride)
    singleOf(::UnregisterClaimMenuOpening)

    // Player / Tool
    singleOf(::GetClaimIdFromMoveTool)
    singleOf(::GivePlayerClaimTool)
    singleOf(::GivePlayerMoveTool)
    singleOf(::IsItemClaimTool)
    singleOf(::IsItemMoveTool)
    singleOf(::SyncToolVisualization)

    // Player / Visualisation
    singleOf(::ClearSelectionVisualisation)
    singleOf(::ClearVisualisation)
    singleOf(::DisplaySelectionVisualisation)
    singleOf(::DisplayVisualisation)
    singleOf(::GetVisualisedClaimBlocks)
    singleOf(::GetVisualiserMode)
    singleOf(::IsPlayerVisualising)
    singleOf(::RefreshVisualisation)
    singleOf(::ScheduleClearVisualisation)
    singleOf(::ToggleVisualiserMode)
}