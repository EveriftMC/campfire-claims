package dev.mizarc.bellclaims.application.services

import dev.mizarc.bellclaims.domain.values.Area
import dev.mizarc.bellclaims.domain.values.Position3D
import java.util.UUID

interface WorldManipulationService {
    fun breakWithoutItemDrop(worldId: UUID, position: Position3D): Boolean
    fun isInsideWorldBorder(worldId: UUID, area: Area): Boolean
}