package com.chillsam.courmy.main.presentation.chat

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 채팅 화면용 임시 샘플 데이터. 서버(메시지 API) 연동 전 UI 를 확인하기 위한 것이다.
 *
 * 채팅홈(참여 중·동네 채팅방 목록)과 채팅방이 같은 방을 바라봐야 하므로 한곳에서 관리한다.
 * 실제 API 가 붙으면 이 object 대신 Repository/UseCase 로 대체한다.
 */
internal object ChatSampleData {
    /** 현재 동네(채팅홈 우측·동네 채팅방 탭 라벨에 쓴다). */
    const val REGION = "연남동"

    /** 방 한 개. 목록 미리보기([previewMessage]·[previewTime]·[unreadCount])와 방 상세([messages])를 함께 담는다. */
    data class Room(
        val id: Long,
        val name: String,
        val regionTag: String?,
        val subtitle: String?,
        val previewMessage: String,
        val previewTime: String,
        val unreadCount: Int,
        val messages: ImmutableList<ChatMessageUi>,
    )

    /** 동네 채팅방 탭의 한 줄. [joined] 면 "참여 중", 아니면 "입장" 버튼을 보인다. */
    data class NeighborhoodRoom(
        val roomId: Long,
        val name: String,
        val description: String?,
        val memberCount: Int,
        val featured: Boolean,
        val joined: Boolean,
    )

    private val rooms: Map<Long, Room> =
        listOf(
            Room(
                id = 1,
                name = "김코스",
                regionTag = null,
                subtitle = null,
                previewMessage = "오늘 같이 코스 돌아보실래요?",
                previewTime = "오후 2:16",
                unreadCount = 2,
                messages =
                    persistentListOf(
                        ChatMessageUi(
                            1,
                            "안녕하세요! 저장하신 성수 코스 잘 봤어요 😊",
                            fromMe = false,
                            time = "오후 2:14",
                            senderName = null,
                        ),
                        ChatMessageUi(
                            2,
                            "오 반가워요! 혹시 비슷한 코스 더 있을까요?",
                            fromMe = true,
                            time = "오후 2:15",
                            senderName = null,
                        ),
                        ChatMessageUi(
                            3,
                            "그럼요, 연남동 쪽으로도 하나 만들어 뒀어요.",
                            fromMe = false,
                            time = "오후 2:15",
                            senderName = null,
                        ),
                        ChatMessageUi(4, "오늘 같이 코스 돌아보실래요?", fromMe = false, time = "오후 2:16", senderName = null),
                    ),
            ),
            Room(
                id = 2,
                name = "연남동 산책 모임",
                regionTag = REGION,
                subtitle = "$REGION · 12명",
                previewMessage = "연남동 경의선 숲길에서 시작해요",
                previewTime = "오전 11:12",
                unreadCount = 0,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "이번 주말 모임 장소 정했어요!", fromMe = false, time = "오전 11:02", senderName = "지호"),
                        ChatMessageUi(2, "오 어디로 가나요?", fromMe = true, time = "오전 11:10", senderName = null),
                        ChatMessageUi(3, "연남동 경의선 숲길에서 시작해요", fromMe = false, time = "오전 11:12", senderName = "지호"),
                        ChatMessageUi(4, "저도 참여해도 될까요?", fromMe = false, time = "오전 11:15", senderName = "하늘"),
                    ),
            ),
            Room(
                id = 3,
                name = "박플랜",
                regionTag = null,
                subtitle = null,
                previewMessage = "다음엔 카페 투어도 같이 가요",
                previewTime = "어제",
                unreadCount = 1,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "코스 후기 남겨주셔서 감사해요!", fromMe = false, time = "어제", senderName = null),
                        ChatMessageUi(2, "덕분에 즐거운 하루였어요 :)", fromMe = true, time = "어제", senderName = null),
                        ChatMessageUi(3, "다음엔 카페 투어도 같이 가요", fromMe = false, time = "어제", senderName = null),
                    ),
            ),
            Room(
                id = 4,
                name = "주말 나들이방",
                regionTag = REGION,
                subtitle = "$REGION · 8명",
                previewMessage = "다들 수고하셨습니다!",
                previewTime = "3일 전",
                unreadCount = 0,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "사진 공유해요 📷", fromMe = false, time = "3일 전", senderName = "민수"),
                        ChatMessageUi(2, "다들 수고하셨습니다!", fromMe = false, time = "3일 전", senderName = "서윤"),
                    ),
            ),
            Room(
                id = 101,
                name = "연남동 라운지",
                regionTag = REGION,
                subtitle = "$REGION · 38명",
                previewMessage = "",
                previewTime = "",
                unreadCount = 0,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "연남동 라운지에 오신 걸 환영해요 🎉", fromMe = false, time = "오전 9:30", senderName = "운영자"),
                        ChatMessageUi(2, "요즘 가볼 만한 곳 추천받아요~", fromMe = false, time = "오전 10:05", senderName = "다은"),
                    ),
            ),
            Room(
                id = 103,
                name = "연트럴파크 맛집",
                regionTag = REGION,
                subtitle = "$REGION · 9명",
                previewMessage = "",
                previewTime = "",
                unreadCount = 0,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "경의선 숲길 근처 맛집 공유방이에요!", fromMe = false, time = "오후 1:20", senderName = "준호"),
                    ),
            ),
            Room(
                id = 104,
                name = "연남 카페 투어",
                regionTag = REGION,
                subtitle = "$REGION · 7명",
                previewMessage = "",
                previewTime = "",
                unreadCount = 0,
                messages =
                    persistentListOf(
                        ChatMessageUi(1, "이번 주 카페 투어 같이 가실 분?", fromMe = false, time = "오후 3:40", senderName = "소연"),
                    ),
            ),
        ).associateBy { it.id }

    /** 참여 중 탭 목록(가입한 방). */
    val participatingIds: List<Long> = listOf(1, 2, 3, 4)

    /** 동네 채팅방 탭 목록. */
    val neighborhoodRooms: ImmutableList<NeighborhoodRoom> =
        persistentListOf(
            NeighborhoodRoom(
                roomId = 101,
                name = "연남동 라운지",
                description = "동네 누구나",
                memberCount = 38,
                featured = true,
                joined = false,
            ),
            NeighborhoodRoom(
                roomId = 2,
                name = "연남동 산책 모임",
                description = null,
                memberCount = 12,
                featured = false,
                joined = true,
            ),
            NeighborhoodRoom(
                roomId = 103,
                name = "연트럴파크 맛집",
                description = null,
                memberCount = 9,
                featured = false,
                joined = false,
            ),
            NeighborhoodRoom(
                roomId = 104,
                name = "연남 카페 투어",
                description = null,
                memberCount = 7,
                featured = false,
                joined = false,
            ),
        )

    fun participating(): List<Room> = participatingIds.mapNotNull { rooms[it] }

    fun find(id: Long): Room? = rooms[id]
}
