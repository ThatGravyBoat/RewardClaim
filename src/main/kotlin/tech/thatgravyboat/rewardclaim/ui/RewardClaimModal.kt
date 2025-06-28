package tech.thatgravyboat.rewardclaim.ui

import earth.terrarium.olympus.client.ui.modals.ActionModal
import tech.thatgravyboat.rewardclaim.mc

class RewardClaimModal(builder: Builder) : ActionModal(builder, null) {

    override fun onClose() = RewardClaimScreens.close()
}

fun ActionModal.Builder.openWithoutBackground() {
    mc.setScreen(RewardClaimModal(this))
}