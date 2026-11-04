package com.chillsam.courmy.main.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 채팅홈 UI: 상단바(채팅·동네) + 세그먼트 탭(참여 중 / 동네 채팅방) + 목록. 행을 누르면 [onRoomEnter]. */
@Composable
internal fun ChatHomeScreen(
    uiState: ChatHomeUIState,
    onTabSelect: (ChatHomeTab) -> Unit,
    onRoomEnter: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        ChatHomeTopBar(region = uiState.region)
        ChatHomeTabRow(
            region = uiState.region,
            selectedTab = uiState.selectedTab,
            onTabSelect = onTabSelect,
        )
        HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            when (uiState.selectedTab) {
                ChatHomeTab.PARTICIPATING -> {
                    items(uiState.conversations, key = { it.roomId }) { conversation ->
                        ConversationRow(conversation = conversation, onClick = { onRoomEnter(conversation.roomId) })
                    }
                }

                ChatHomeTab.NEIGHBORHOOD -> {
                    items(uiState.neighborhoodRooms, key = { it.roomId }) { room ->
                        NeighborhoodRow(room = room, onEnter = { onRoomEnter(room.roomId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatHomeTopBar(region: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "채팅",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_location_24),
            contentDescription = null,
            tint = color.contentAccent,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(4.dp))
        DsText(
            text = region,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel1,
        )
    }
}

@Composable
private fun ChatHomeTabRow(
    region: String,
    selectedTab: ChatHomeTab,
    onTabSelect: (ChatHomeTab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ChatHomeTab(
            label = "참여 중",
            selected = selectedTab == ChatHomeTab.PARTICIPATING,
            onClick = { onTabSelect(ChatHomeTab.PARTICIPATING) },
        )
        ChatHomeTab(
            label = "$region 채팅방",
            selected = selectedTab == ChatHomeTab.NEIGHBORHOOD,
            onClick = { onTabSelect(ChatHomeTab.NEIGHBORHOOD) },
        )
    }
}

@Composable
private fun ChatHomeTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .width(IntrinsicSize.Max)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = if (selected) color.contentDefaultLevel0 else color.contentDefaultLevel3,
            modifier = Modifier.padding(vertical = 14.dp),
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(if (selected) color.contentAccent else color.bgDefaultLevel1),
        )
    }
}

@Composable
private fun ConversationRow(
    conversation: ConversationUi,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(color.imagePlaceholder))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DsText(
                    text = conversation.name,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                if (conversation.regionTag != null) {
                    Spacer(Modifier.width(6.dp))
                    RegionChip(text = conversation.regionTag)
                }
            }
            Spacer(Modifier.height(4.dp))
            DsText(
                text = conversation.lastMessage,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            DsText(
                text = conversation.time,
                style = DesignSystemThemeImpl.typeScale.textExtraXS,
                color = color.contentDefaultLevel2,
            )
            if (conversation.unreadCount > 0) {
                Spacer(Modifier.height(6.dp))
                UnreadBadge(count = conversation.unreadCount)
            }
        }
    }
}

@Composable
private fun NeighborhoodRow(
    room: NeighborhoodRoomUi,
    onEnter: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onEnter)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(if (room.featured) color.bgAccent else color.imagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            if (room.featured) {
                DsText(
                    text = room.initial,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentOnAccent,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = room.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            Spacer(Modifier.height(4.dp))
            DsText(
                text = room.subtitle,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
        Spacer(Modifier.width(8.dp))
        if (room.joined) {
            DsText(
                text = "참여 중",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        } else {
            EnterButton(onClick = onEnter)
        }
    }
}

@Composable
private fun EnterButton(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 7.dp),
    ) {
        DsText(
            text = "입장",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
    }
}

@Composable
private fun RegionChip(text: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.bgAccentSubtle)
                .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textExtraXS,
            color = color.contentAccent,
        )
    }
}

@Composable
private fun UnreadBadge(count: Int) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(color.bgAccent),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = if (count > 99) "99+" else count.toString(),
            style = DesignSystemThemeImpl.typeScale.textExtraXS,
            color = color.contentOnAccent,
        )
    }
}
