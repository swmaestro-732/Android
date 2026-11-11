# 정확한 리소스 문구 (ko/en/ja)

변경된 XML 그룹의 전체 키. Android XML escape와 format 인자를 보존한다. 미제공 번역은 기본 리소스 fallback이며 translatable=false 브랜드도 포함한다.

## common/presentation/src/main/res/values/ai_chat_messages.xml

### message_ai_error

```xml
<!-- ko -->
<string name="message_ai_error">지금은 추천을 불러오지 못했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_ai_error">Could not load recommendations. Please try again shortly.</string>
<!-- ja -->
<string name="message_ai_error">おすすめを読み込めませんでした。しばらくしてから再度お試しください。</string>
```

### message_ai_greeting

```xml
<!-- ko -->
<string name="message_ai_greeting">안녕하세요, 커미 AI예요!\n먼저, 어느 지역으로 떠나볼까요?</string>
<!-- en -->
<string name="message_ai_greeting">Hi, I am Courmy AI!\nWhich area would you like to explore?</string>
<!-- ja -->
<string name="message_ai_greeting">こんにちは、クルミ AIです！\nまず、どのエリアに出かけましょうか？</string>
```

### message_ai_region_placeholder

```xml
<!-- ko -->
<string name="message_ai_region_placeholder">지역을 입력해도 돼요</string>
<!-- en -->
<string name="message_ai_region_placeholder">You can enter an area</string>
<!-- ja -->
<string name="message_ai_region_placeholder">エリアを入力できます</string>
```

### message_ai_session_expired

```xml
<!-- ko -->
<string name="message_ai_session_expired">대화가 만료돼서 처음부터 다시 시작할게요.</string>
<!-- en -->
<string name="message_ai_session_expired">This conversation has expired. Let us start again.</string>
<!-- ja -->
<string name="message_ai_session_expired">会話の有効期限が切れました。最初からやり直します。</string>
```

## common/presentation/src/main/res/values/brand_strings.xml

### common_ai_label

```xml
<!-- ko -->
<string name="common_ai_label" translatable="false">AI</string>
<!-- en -->
<!-- default fallback -->
<!-- ja -->
<!-- default fallback -->
```

### language_name_english

```xml
<!-- ko -->
<string name="language_name_english" translatable="false">English</string>
<!-- en -->
<!-- default fallback -->
<!-- ja -->
<!-- default fallback -->
```

### language_name_japanese

```xml
<!-- ko -->
<string name="language_name_japanese" translatable="false">日本語</string>
<!-- en -->
<!-- default fallback -->
<!-- ja -->
<!-- default fallback -->
```

### language_name_korean

```xml
<!-- ko -->
<string name="language_name_korean" translatable="false">한국어</string>
<!-- en -->
<!-- default fallback -->
<!-- ja -->
<!-- default fallback -->
```

## common/presentation/src/main/res/values/notification_channel.xml

### notification_channel_activity

```xml
<!-- ko -->
<string name="notification_channel_activity">활동 알림</string>
<!-- en -->
<string name="notification_channel_activity">Activity notifications</string>
<!-- ja -->
<string name="notification_channel_activity">アクティビティ通知</string>
```

## common/presentation/src/main/res/values/relative_time.xml

### relative_day

```xml
<!-- ko -->
<plurals name="relative_day">
        <item quantity="one">%1$d일 전</item>
        <item quantity="other">%1$d일 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_day">
        <item quantity="one">%1$d day ago</item>
        <item quantity="other">%1$d days ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_day">
        <item quantity="one">%1$d日前</item>
        <item quantity="other">%1$d日前</item>
    </plurals>
```

### relative_hour

```xml
<!-- ko -->
<plurals name="relative_hour">
        <item quantity="one">%1$d시간 전</item>
        <item quantity="other">%1$d시간 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_hour">
        <item quantity="one">%1$d hour ago</item>
        <item quantity="other">%1$d hours ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_hour">
        <item quantity="one">%1$d時間前</item>
        <item quantity="other">%1$d時間前</item>
    </plurals>
```

### relative_just_now

```xml
<!-- ko -->
<string name="relative_just_now">방금 전</string>
<!-- en -->
<string name="relative_just_now">Just now</string>
<!-- ja -->
<string name="relative_just_now">たった今</string>
```

### relative_minute

```xml
<!-- ko -->
<plurals name="relative_minute">
        <item quantity="one">%1$d분 전</item>
        <item quantity="other">%1$d분 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_minute">
        <item quantity="one">%1$d minute ago</item>
        <item quantity="other">%1$d minutes ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_minute">
        <item quantity="one">%1$d分前</item>
        <item quantity="other">%1$d分前</item>
    </plurals>
```

### relative_month

```xml
<!-- ko -->
<plurals name="relative_month">
        <item quantity="one">%1$d개월 전</item>
        <item quantity="other">%1$d개월 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_month">
        <item quantity="one">%1$d month ago</item>
        <item quantity="other">%1$d months ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_month">
        <item quantity="one">%1$dか月前</item>
        <item quantity="other">%1$dか月前</item>
    </plurals>
```

### relative_week

```xml
<!-- ko -->
<plurals name="relative_week">
        <item quantity="one">%1$d주 전</item>
        <item quantity="other">%1$d주 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_week">
        <item quantity="one">%1$d week ago</item>
        <item quantity="other">%1$d weeks ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_week">
        <item quantity="one">%1$d週間前</item>
        <item quantity="other">%1$d週間前</item>
    </plurals>
```

### relative_year

```xml
<!-- ko -->
<plurals name="relative_year">
        <item quantity="one">%1$d년 전</item>
        <item quantity="other">%1$d년 전</item>
    </plurals>
<!-- en -->
<plurals name="relative_year">
        <item quantity="one">%1$d year ago</item>
        <item quantity="other">%1$d years ago</item>
    </plurals>
<!-- ja -->
<plurals name="relative_year">
        <item quantity="one">%1$d年前</item>
        <item quantity="other">%1$d年前</item>
    </plurals>
```

## common/presentation/src/main/res/values/strings.xml

### a11y_more

```xml
<!-- ko -->
<string name="a11y_more">더 보기</string>
<!-- en -->
<string name="a11y_more">More</string>
<!-- ja -->
<string name="a11y_more">もっと見る</string>
```

### app_name

```xml
<!-- ko -->
<string name="app_name">Courmy</string>
<!-- en -->
<string name="app_name">Courmy</string>
<!-- ja -->
<string name="app_name">Courmy</string>
```

### common_cancel

```xml
<!-- ko -->
<string name="common_cancel">취소</string>
<!-- en -->
<string name="common_cancel">Cancel</string>
<!-- ja -->
<string name="common_cancel">キャンセル</string>
```

### common_label_companion_couple

```xml
<!-- ko -->
<string name="common_label_companion_couple">연인</string>
<!-- en -->
<string name="common_label_companion_couple">Partner</string>
<!-- ja -->
<string name="common_label_companion_couple">恋人</string>
```

### common_label_companion_family

```xml
<!-- ko -->
<string name="common_label_companion_family">가족</string>
<!-- en -->
<string name="common_label_companion_family">Family</string>
<!-- ja -->
<string name="common_label_companion_family">家族</string>
```

### common_label_companion_friend

```xml
<!-- ko -->
<string name="common_label_companion_friend">친구</string>
<!-- en -->
<string name="common_label_companion_friend">Friends</string>
<!-- ja -->
<string name="common_label_companion_friend">友人</string>
```

### common_label_companion_solo

```xml
<!-- ko -->
<string name="common_label_companion_solo">혼자</string>
<!-- en -->
<string name="common_label_companion_solo">Solo</string>
<!-- ja -->
<string name="common_label_companion_solo">一人</string>
```

### common_label_group_access

```xml
<!-- ko -->
<string name="common_label_group_access">접근·편의</string>
<!-- en -->
<string name="common_label_group_access">Access and convenience</string>
<!-- ja -->
<string name="common_label_group_access">アクセス・利便性</string>
```

### common_label_group_companions

```xml
<!-- ko -->
<string name="common_label_group_companions">동반자</string>
<!-- en -->
<string name="common_label_group_companions">Companions</string>
<!-- ja -->
<string name="common_label_group_companions">同行者</string>
```

### common_label_group_course_structure

```xml
<!-- ko -->
<string name="common_label_group_course_structure">코스 구성</string>
<!-- en -->
<string name="common_label_group_course_structure">Course layout</string>
<!-- ja -->
<string name="common_label_group_course_structure">コース構成</string>
```

### common_label_group_environment

```xml
<!-- ko -->
<string name="common_label_group_environment">공간·환경</string>
<!-- en -->
<string name="common_label_group_environment">Space and atmosphere</string>
<!-- ja -->
<string name="common_label_group_environment">空間・環境</string>
```

### common_label_group_pace

```xml
<!-- ko -->
<string name="common_label_group_pace">시간·페이스</string>
<!-- en -->
<string name="common_label_group_pace">Time and pace</string>
<!-- ja -->
<string name="common_label_group_pace">時間・ペース</string>
```

### common_label_group_service

```xml
<!-- ko -->
<string name="common_label_group_service">응대·서비스</string>
<!-- en -->
<string name="common_label_group_service">Service</string>
<!-- ja -->
<string name="common_label_group_service">接客・サービス</string>
```

### common_label_group_transport

```xml
<!-- ko -->
<string name="common_label_group_transport">이동·접근</string>
<!-- en -->
<string name="common_label_group_transport">Getting around</string>
<!-- ja -->
<string name="common_label_group_transport">移動・アクセス</string>
```

### common_label_group_value

```xml
<!-- ko -->
<string name="common_label_group_value">비용·만족</string>
<!-- en -->
<string name="common_label_group_value">Value and satisfaction</string>
<!-- ja -->
<string name="common_label_group_value">費用・満足度</string>
```

### common_label_island_dokdo

```xml
<!-- ko -->
<string name="common_label_island_dokdo">독도</string>
<!-- en -->
<string name="common_label_island_dokdo">Dokdo</string>
<!-- ja -->
<string name="common_label_island_dokdo">独島</string>
```

### common_label_island_ulleungdo

```xml
<!-- ko -->
<string name="common_label_island_ulleungdo">울릉도</string>
<!-- en -->
<string name="common_label_island_ulleungdo">Ulleungdo</string>
<!-- ja -->
<string name="common_label_island_ulleungdo">鬱陵島</string>
```

### common_label_islands_baengnyeong_daecheong

```xml
<!-- ko -->
<string name="common_label_islands_baengnyeong_daecheong">백령·대청</string>
<!-- en -->
<string name="common_label_islands_baengnyeong_daecheong">Baengnyeong · Daecheong</string>
<!-- ja -->
<string name="common_label_islands_baengnyeong_daecheong">白翎・大青</string>
```

### common_label_islands_east

```xml
<!-- ko -->
<string name="common_label_islands_east">울릉도·독도</string>
<!-- en -->
<string name="common_label_islands_east">Ulleungdo · Dokdo</string>
<!-- ja -->
<string name="common_label_islands_east">鬱陵島・独島</string>
```

### common_label_islands_west

```xml
<!-- ko -->
<string name="common_label_islands_west">백령·대청·연평</string>
<!-- en -->
<string name="common_label_islands_west">Baengnyeong · Daecheong · Yeonpyeong</string>
<!-- ja -->
<string name="common_label_islands_west">白翎・大青・延坪</string>
```

### common_label_islands_yeonpyeong_area

```xml
<!-- ko -->
<string name="common_label_islands_yeonpyeong_area">연평도 일대</string>
<!-- en -->
<string name="common_label_islands_yeonpyeong_area">Yeonpyeong area</string>
<!-- ja -->
<string name="common_label_islands_yeonpyeong_area">延坪島周辺</string>
```

### common_label_region_busan

```xml
<!-- ko -->
<string name="common_label_region_busan">부산</string>
<!-- en -->
<string name="common_label_region_busan">Busan</string>
<!-- ja -->
<string name="common_label_region_busan">釜山</string>
```

### common_label_region_chungbuk

```xml
<!-- ko -->
<string name="common_label_region_chungbuk">충북</string>
<!-- en -->
<string name="common_label_region_chungbuk">Chungbuk</string>
<!-- ja -->
<string name="common_label_region_chungbuk">忠北</string>
```

### common_label_region_chungnam

```xml
<!-- ko -->
<string name="common_label_region_chungnam">충남</string>
<!-- en -->
<string name="common_label_region_chungnam">Chungnam</string>
<!-- ja -->
<string name="common_label_region_chungnam">忠南</string>
```

### common_label_region_daegu

```xml
<!-- ko -->
<string name="common_label_region_daegu">대구</string>
<!-- en -->
<string name="common_label_region_daegu">Daegu</string>
<!-- ja -->
<string name="common_label_region_daegu">大邱</string>
```

### common_label_region_daejeon

```xml
<!-- ko -->
<string name="common_label_region_daejeon">대전</string>
<!-- en -->
<string name="common_label_region_daejeon">Daejeon</string>
<!-- ja -->
<string name="common_label_region_daejeon">大田</string>
```

### common_label_region_gangwon

```xml
<!-- ko -->
<string name="common_label_region_gangwon">강원</string>
<!-- en -->
<string name="common_label_region_gangwon">Gangwon</string>
<!-- ja -->
<string name="common_label_region_gangwon">江原</string>
```

### common_label_region_gwangju

```xml
<!-- ko -->
<string name="common_label_region_gwangju">광주</string>
<!-- en -->
<string name="common_label_region_gwangju">Gwangju</string>
<!-- ja -->
<string name="common_label_region_gwangju">光州</string>
```

### common_label_region_gyeongbuk

```xml
<!-- ko -->
<string name="common_label_region_gyeongbuk">경북</string>
<!-- en -->
<string name="common_label_region_gyeongbuk">Gyeongbuk</string>
<!-- ja -->
<string name="common_label_region_gyeongbuk">慶北</string>
```

### common_label_region_gyeonggi

```xml
<!-- ko -->
<string name="common_label_region_gyeonggi">경기</string>
<!-- en -->
<string name="common_label_region_gyeonggi">Gyeonggi</string>
<!-- ja -->
<string name="common_label_region_gyeonggi">京畿</string>
```

### common_label_region_gyeongnam

```xml
<!-- ko -->
<string name="common_label_region_gyeongnam">경남</string>
<!-- en -->
<string name="common_label_region_gyeongnam">Gyeongnam</string>
<!-- ja -->
<string name="common_label_region_gyeongnam">慶南</string>
```

### common_label_region_incheon

```xml
<!-- ko -->
<string name="common_label_region_incheon">인천</string>
<!-- en -->
<string name="common_label_region_incheon">Incheon</string>
<!-- ja -->
<string name="common_label_region_incheon">仁川</string>
```

### common_label_region_jeju

```xml
<!-- ko -->
<string name="common_label_region_jeju">제주</string>
<!-- en -->
<string name="common_label_region_jeju">Jeju</string>
<!-- ja -->
<string name="common_label_region_jeju">済州</string>
```

### common_label_region_jeonbuk

```xml
<!-- ko -->
<string name="common_label_region_jeonbuk">전북</string>
<!-- en -->
<string name="common_label_region_jeonbuk">Jeonbuk</string>
<!-- ja -->
<string name="common_label_region_jeonbuk">全北</string>
```

### common_label_region_jeonnam

```xml
<!-- ko -->
<string name="common_label_region_jeonnam">전남</string>
<!-- en -->
<string name="common_label_region_jeonnam">Jeonnam</string>
<!-- ja -->
<string name="common_label_region_jeonnam">全南</string>
```

### common_label_region_sejong

```xml
<!-- ko -->
<string name="common_label_region_sejong">세종</string>
<!-- en -->
<string name="common_label_region_sejong">Sejong</string>
<!-- ja -->
<string name="common_label_region_sejong">世宗</string>
```

### common_label_region_seoul

```xml
<!-- ko -->
<string name="common_label_region_seoul">서울</string>
<!-- en -->
<string name="common_label_region_seoul">Seoul</string>
<!-- ja -->
<string name="common_label_region_seoul">ソウル</string>
```

### common_label_region_ulleung

```xml
<!-- ko -->
<string name="common_label_region_ulleung">울릉</string>
<!-- en -->
<string name="common_label_region_ulleung">Ulleung</string>
<!-- ja -->
<string name="common_label_region_ulleung">鬱陵</string>
```

### common_label_region_ulsan

```xml
<!-- ko -->
<string name="common_label_region_ulsan">울산</string>
<!-- en -->
<string name="common_label_region_ulsan">Ulsan</string>
<!-- ja -->
<string name="common_label_region_ulsan">蔚山</string>
```

### common_label_report_abuse

```xml
<!-- ko -->
<string name="common_label_report_abuse">욕설·비방</string>
<!-- en -->
<string name="common_label_report_abuse">Abusive language or harassment</string>
<!-- ja -->
<string name="common_label_report_abuse">暴言・誹謗中傷</string>
```

### common_label_report_obscene

```xml
<!-- ko -->
<string name="common_label_report_obscene">음란한 내용</string>
<!-- en -->
<string name="common_label_report_obscene">Sexually explicit content</string>
<!-- ja -->
<string name="common_label_report_obscene">わいせつな内容</string>
```

### common_label_report_privacy

```xml
<!-- ko -->
<string name="common_label_report_privacy">개인정보 침해</string>
<!-- en -->
<string name="common_label_report_privacy">Privacy violation</string>
<!-- ja -->
<string name="common_label_report_privacy">個人情報の侵害</string>
```

### common_label_report_spam

```xml
<!-- ko -->
<string name="common_label_report_spam">스팸·광고</string>
<!-- en -->
<string name="common_label_report_spam">Spam or advertising</string>
<!-- ja -->
<string name="common_label_report_spam">スパム・広告</string>
```

### common_label_review_air

```xml
<!-- ko -->
<string name="common_label_review_air">공기가 맑아요</string>
<!-- en -->
<string name="common_label_review_air">Fresh air</string>
<!-- ja -->
<string name="common_label_review_air">空気が澄んでいます</string>
```

### common_label_review_architecture

```xml
<!-- ko -->
<string name="common_label_review_architecture">건축이 멋져요</string>
<!-- en -->
<string name="common_label_review_architecture">Beautiful architecture</string>
<!-- ja -->
<string name="common_label_review_architecture">建築が素敵です</string>
```

### common_label_review_artwork

```xml
<!-- ko -->
<string name="common_label_review_artwork">작품이 인상적이에요</string>
<!-- en -->
<string name="common_label_review_artwork">Impressive artwork</string>
<!-- ja -->
<string name="common_label_review_artwork">作品が印象的です</string>
```

### common_label_review_beginner

```xml
<!-- ko -->
<string name="common_label_review_beginner">초보도 하기 좋아요</string>
<!-- en -->
<string name="common_label_review_beginner">Beginner-friendly</string>
<!-- ja -->
<string name="common_label_review_beginner">初心者でも楽しめます</string>
```

### common_label_review_browsing

```xml
<!-- ko -->
<string name="common_label_review_browsing">편하게 둘러볼 수 있어요</string>
<!-- en -->
<string name="common_label_review_browsing">Easy to browse at your own pace</string>
<!-- ja -->
<string name="common_label_review_browsing">気軽に見て回れます</string>
```

### common_label_review_bycar

```xml
<!-- ko -->
<string name="common_label_review_bycar">차 있으면 편해요</string>
<!-- en -->
<string name="common_label_review_bycar">Easier with a car</string>
<!-- ja -->
<string name="common_label_review_bycar">車があると便利です</string>
```

### common_label_review_calm

```xml
<!-- ko -->
<string name="common_label_review_calm">마음이 편안해져요</string>
<!-- en -->
<string name="common_label_review_calm">Puts your mind at ease</string>
<!-- ja -->
<string name="common_label_review_calm">心が落ち着きます</string>
```

### common_label_review_clean

```xml
<!-- ko -->
<string name="common_label_review_clean">매장이 깨끗해요</string>
<!-- en -->
<string name="common_label_review_clean">Clean store</string>
<!-- ja -->
<string name="common_label_review_clean">店内がきれいです</string>
```

### common_label_review_coffee

```xml
<!-- ko -->
<string name="common_label_review_coffee">커피가 맛있어요</string>
<!-- en -->
<string name="common_label_review_coffee">Delicious coffee</string>
<!-- ja -->
<string name="common_label_review_coffee">コーヒーがおいしいです</string>
```

### common_label_review_combo

```xml
<!-- ko -->
<string name="common_label_review_combo">장소 조합이 좋아요</string>
<!-- en -->
<string name="common_label_review_combo">Great mix of places</string>
<!-- ja -->
<string name="common_label_review_combo">場所の組み合わせが良いです</string>
```

### common_label_review_compact

```xml
<!-- ko -->
<string name="common_label_review_compact">이동 거리가 짧아요</string>
<!-- en -->
<string name="common_label_review_compact">Short distances between stops</string>
<!-- ja -->
<string name="common_label_review_compact">移動距離が短いです</string>
```

### common_label_review_course_date

```xml
<!-- ko -->
<string name="common_label_review_course_date">데이트 코스로 좋아요</string>
<!-- en -->
<string name="common_label_review_course_date">Great date course</string>
<!-- ja -->
<string name="common_label_review_course_date">デートコースにおすすめです</string>
```

### common_label_review_course_family

```xml
<!-- ko -->
<string name="common_label_review_course_family">가족 나들이로 좋아요</string>
<!-- en -->
<string name="common_label_review_course_family">Great for a family outing</string>
<!-- ja -->
<string name="common_label_review_course_family">家族のお出かけにおすすめです</string>
```

### common_label_review_course_revisit

```xml
<!-- ko -->
<string name="common_label_review_course_revisit">또 가고 싶어요</string>
<!-- en -->
<string name="common_label_review_course_revisit">Would visit again</string>
<!-- ja -->
<string name="common_label_review_course_revisit">また行きたいです</string>
```

### common_label_review_course_solo

```xml
<!-- ko -->
<string name="common_label_review_course_solo">혼자 즐기기 좋아요</string>
<!-- en -->
<string name="common_label_review_course_solo">Great to enjoy solo</string>
<!-- ja -->
<string name="common_label_review_course_solo">一人でも楽しめます</string>
```

### common_label_review_course_value

```xml
<!-- ko -->
<string name="common_label_review_course_value">가성비 좋은 코스예요</string>
<!-- en -->
<string name="common_label_review_course_value">Good-value course</string>
<!-- ja -->
<string name="common_label_review_course_value">お得に楽しめるコースです</string>
```

### common_label_review_cozy

```xml
<!-- ko -->
<string name="common_label_review_cozy">조용하고 아늑해요</string>
<!-- en -->
<string name="common_label_review_cozy">Quiet and cozy</string>
<!-- ja -->
<string name="common_label_review_cozy">静かで居心地が良いです</string>
```

### common_label_review_curation

```xml
<!-- ko -->
<string name="common_label_review_curation">큐레이션이 좋아요</string>
<!-- en -->
<string name="common_label_review_curation">Thoughtful curation</string>
<!-- ja -->
<string name="common_label_review_curation">展示の構成が魅力的です</string>
```

### common_label_review_dessert

```xml
<!-- ko -->
<string name="common_label_review_dessert">디저트가 맛있어요</string>
<!-- en -->
<string name="common_label_review_dessert">Delicious desserts</string>
<!-- ja -->
<string name="common_label_review_dessert">デザートがおいしいです</string>
```

### common_label_review_discovery

```xml
<!-- ko -->
<string name="common_label_review_discovery">구경하는 재미가 있어요</string>
<!-- en -->
<string name="common_label_review_discovery">Fun to browse</string>
<!-- ja -->
<string name="common_label_review_discovery">見て回るのが楽しいです</string>
```

### common_label_review_diverse

```xml
<!-- ko -->
<string name="common_label_review_diverse">즐길 거리가 다양해요</string>
<!-- en -->
<string name="common_label_review_diverse">Lots of things to do</string>
<!-- ja -->
<string name="common_label_review_diverse">楽しみ方が豊富です</string>
```

### common_label_review_drinks

```xml
<!-- ko -->
<string name="common_label_review_drinks">술 종류가 다양해요</string>
<!-- en -->
<string name="common_label_review_drinks">Wide selection of drinks</string>
<!-- ja -->
<string name="common_label_review_drinks">お酒の種類が豊富です</string>
```

### common_label_review_efficient

```xml
<!-- ko -->
<string name="common_label_review_efficient">동선이 효율적이에요</string>
<!-- en -->
<string name="common_label_review_efficient">Efficient route</string>
<!-- ja -->
<string name="common_label_review_efficient">効率よく回れます</string>
```

### common_label_review_exciting

```xml
<!-- ko -->
<string name="common_label_review_exciting">신나고 재밌어요</string>
<!-- en -->
<string name="common_label_review_exciting">Exciting and fun</string>
<!-- ja -->
<string name="common_label_review_exciting">わくわくして楽しいです</string>
```

### common_label_review_exclusive

```xml
<!-- ko -->
<string name="common_label_review_exclusive">여기서만 파는 게 있어요</string>
<!-- en -->
<string name="common_label_review_exclusive">Unique items sold only here</string>
<!-- ja -->
<string name="common_label_review_exclusive">ここだけの商品があります</string>
```

### common_label_review_expert

```xml
<!-- ko -->
<string name="common_label_review_expert">관리가 전문적이에요</string>
<!-- en -->
<string name="common_label_review_expert">Professional care</string>
<!-- ja -->
<string name="common_label_review_expert">専門的なケアを受けられます</string>
```

### common_label_review_findable

```xml
<!-- ko -->
<string name="common_label_review_findable">찾기 쉬워요</string>
<!-- en -->
<string name="common_label_review_findable">Easy to find</string>
<!-- ja -->
<string name="common_label_review_findable">見つけやすい場所です</string>
```

### common_label_review_focus

```xml
<!-- ko -->
<string name="common_label_review_focus">집중하기 좋아요</string>
<!-- en -->
<string name="common_label_review_focus">Easy to focus</string>
<!-- ja -->
<string name="common_label_review_focus">集中しやすいです</string>
```

### common_label_review_free

```xml
<!-- ko -->
<string name="common_label_review_free">무료로 즐길 수 있어요</string>
<!-- en -->
<string name="common_label_review_free">Enjoy for free</string>
<!-- ja -->
<string name="common_label_review_free">無料で楽しめます</string>
```

### common_label_review_fresh

```xml
<!-- ko -->
<string name="common_label_review_fresh">재료가 신선해요</string>
<!-- en -->
<string name="common_label_review_fresh">Fresh ingredients</string>
<!-- ja -->
<string name="common_label_review_fresh">食材が新鮮です</string>
```

### common_label_review_friendly

```xml
<!-- ko -->
<string name="common_label_review_friendly">사장님이 친절해요</string>
<!-- en -->
<string name="common_label_review_friendly">Friendly owner</string>
<!-- ja -->
<string name="common_label_review_friendly">店主が親切です</string>
```

### common_label_review_friends

```xml
<!-- ko -->
<string name="common_label_review_friends">친구랑 가기 좋아요</string>
<!-- en -->
<string name="common_label_review_friends">Great with friends</string>
<!-- ja -->
<string name="common_label_review_friends">友人とのお出かけにおすすめです</string>
```

### common_label_review_fullday

```xml
<!-- ko -->
<string name="common_label_review_fullday">하루 코스로 좋아요</string>
<!-- en -->
<string name="common_label_review_fullday">Great for a full day</string>
<!-- ja -->
<string name="common_label_review_fullday">一日のお出かけにぴったりです</string>
```

### common_label_review_gifts

```xml
<!-- ko -->
<string name="common_label_review_gifts">선물 고르기 좋아요</string>
<!-- en -->
<string name="common_label_review_gifts">Great for gift shopping</string>
<!-- ja -->
<string name="common_label_review_gifts">プレゼント選びにぴったりです</string>
```

### common_label_review_group

```xml
<!-- ko -->
<string name="common_label_review_group">회식하기 좋아요</string>
<!-- en -->
<string name="common_label_review_group">Great for work gatherings</string>
<!-- ja -->
<string name="common_label_review_group">職場の集まりにおすすめです</string>
```

### common_label_review_guidance

```xml
<!-- ko -->
<string name="common_label_review_guidance">설명이 자세해요</string>
<!-- en -->
<string name="common_label_review_guidance">Detailed guidance</string>
<!-- ja -->
<string name="common_label_review_guidance">説明が丁寧です</string>
```

### common_label_review_halfday

```xml
<!-- ko -->
<string name="common_label_review_halfday">반나절 코스로 좋아요</string>
<!-- en -->
<string name="common_label_review_halfday">Great for a half-day trip</string>
<!-- ja -->
<string name="common_label_review_halfday">半日のお出かけにぴったりです</string>
```

### common_label_review_healing

```xml
<!-- ko -->
<string name="common_label_review_healing">힐링하기 좋아요</string>
<!-- en -->
<string name="common_label_review_healing">Great for unwinding</string>
<!-- ja -->
<string name="common_label_review_healing">リフレッシュできます</string>
```

### common_label_review_hearty

```xml
<!-- ko -->
<string name="common_label_review_hearty">양이 푸짐해요</string>
<!-- en -->
<string name="common_label_review_hearty">Generous portions</string>
<!-- ja -->
<string name="common_label_review_hearty">ボリュームたっぷりです</string>
```

### common_label_review_helpful

```xml
<!-- ko -->
<string name="common_label_review_helpful">설명을 잘해줘요</string>
<!-- en -->
<string name="common_label_review_helpful">Helpful explanations</string>
<!-- ja -->
<string name="common_label_review_helpful">説明がわかりやすいです</string>
```

### common_label_review_hygiene

```xml
<!-- ko -->
<string name="common_label_review_hygiene">시설이 청결해요</string>
<!-- en -->
<string name="common_label_review_hygiene">Clean facilities</string>
<!-- ja -->
<string name="common_label_review_hygiene">施設が清潔です</string>
```

### common_label_review_immersive

```xml
<!-- ko -->
<string name="common_label_review_immersive">시간 가는 줄 몰라요</string>
<!-- en -->
<string name="common_label_review_immersive">Time flies here</string>
<!-- ja -->
<string name="common_label_review_immersive">時間を忘れて楽しめます</string>
```

### common_label_review_interior

```xml
<!-- ko -->
<string name="common_label_review_interior">인테리어가 예뻐요</string>
<!-- en -->
<string name="common_label_review_interior">Beautiful interior</string>
<!-- ja -->
<string name="common_label_review_interior">内装がおしゃれです</string>
```

### common_label_review_kids

```xml
<!-- ko -->
<string name="common_label_review_kids">아이와 함께 좋아요</string>
<!-- en -->
<string name="common_label_review_kids">Great with kids</string>
<!-- ja -->
<string name="common_label_review_kids">子どもと一緒に楽しめます</string>
```

### common_label_review_late

```xml
<!-- ko -->
<string name="common_label_review_late">늦게까지 해요</string>
<!-- en -->
<string name="common_label_review_late">Open late</string>
<!-- ja -->
<string name="common_label_review_late">遅くまで営業しています</string>
```

### common_label_review_lingering

```xml
<!-- ko -->
<string name="common_label_review_lingering">오래 있기 좋아요</string>
<!-- en -->
<string name="common_label_review_lingering">Great for a longer stay</string>
<!-- ja -->
<string name="common_label_review_lingering">ゆっくり過ごせます</string>
```

### common_label_review_materials

```xml
<!-- ko -->
<string name="common_label_review_materials">재료가 좋아요</string>
<!-- en -->
<string name="common_label_review_materials">Quality materials</string>
<!-- ja -->
<string name="common_label_review_materials">材料の質が良いです</string>
```

### common_label_review_memorable

```xml
<!-- ko -->
<string name="common_label_review_memorable">기억에 남아요</string>
<!-- en -->
<string name="common_label_review_memorable">Memorable experience</string>
<!-- ja -->
<string name="common_label_review_memorable">思い出に残ります</string>
```

### common_label_review_metime

```xml
<!-- ko -->
<string name="common_label_review_metime">나를 위한 시간이에요</string>
<!-- en -->
<string name="common_label_review_metime">Time just for yourself</string>
<!-- ja -->
<string name="common_label_review_metime">自分のための時間を過ごせます</string>
```

### common_label_review_mood

```xml
<!-- ko -->
<string name="common_label_review_mood">분위기가 좋아요</string>
<!-- en -->
<string name="common_label_review_mood">Great atmosphere</string>
<!-- ja -->
<string name="common_label_review_mood">雰囲気が良いです</string>
```

### common_label_review_nowait

```xml
<!-- ko -->
<string name="common_label_review_nowait">웨이팅이 적어요</string>
<!-- en -->
<string name="common_label_review_nowait">Little waiting</string>
<!-- ja -->
<string name="common_label_review_nowait">待ち時間が短いです</string>
```

### common_label_review_outlets

```xml
<!-- ko -->
<string name="common_label_review_outlets">콘센트가 많아요</string>
<!-- en -->
<string name="common_label_review_outlets">Plenty of power outlets</string>
<!-- ja -->
<string name="common_label_review_outlets">コンセントが多いです</string>
```

### common_label_review_packed

```xml
<!-- ko -->
<string name="common_label_review_packed">구성이 알차요</string>
<!-- en -->
<string name="common_label_review_packed">Well-planned course</string>
<!-- ja -->
<string name="common_label_review_packed">充実したコースです</string>
```

### common_label_review_parents

```xml
<!-- ko -->
<string name="common_label_review_parents">부모님과 가기 좋아요</string>
<!-- en -->
<string name="common_label_review_parents">Great with parents</string>
<!-- ja -->
<string name="common_label_review_parents">両親とのお出かけにおすすめです</string>
```

### common_label_review_parking

```xml
<!-- ko -->
<string name="common_label_review_parking">주차하기 편해요</string>
<!-- en -->
<string name="common_label_review_parking">Convenient parking</string>
<!-- ja -->
<string name="common_label_review_parking">駐車しやすいです</string>
```

### common_label_review_peaceful

```xml
<!-- ko -->
<string name="common_label_review_peaceful">한적해서 여유로워요</string>
<!-- en -->
<string name="common_label_review_peaceful">Peaceful and uncrowded</string>
<!-- ja -->
<string name="common_label_review_peaceful">人が少なくゆっくり過ごせます</string>
```

### common_label_review_pet

```xml
<!-- ko -->
<string name="common_label_review_pet">반려동물과 오기 좋아요</string>
<!-- en -->
<string name="common_label_review_pet">Great with pets</string>
<!-- ja -->
<string name="common_label_review_pet">ペットと一緒に楽しめます</string>
```

### common_label_review_photo

```xml
<!-- ko -->
<string name="common_label_review_photo">사진 찍기 좋아요</string>
<!-- en -->
<string name="common_label_review_photo">Great for photos</string>
<!-- ja -->
<string name="common_label_review_photo">写真撮影にぴったりです</string>
```

### common_label_review_place_date

```xml
<!-- ko -->
<string name="common_label_review_place_date">데이트하기 좋아요</string>
<!-- en -->
<string name="common_label_review_place_date">Great for dates</string>
<!-- ja -->
<string name="common_label_review_place_date">デートにおすすめです</string>
```

### common_label_review_place_family

```xml
<!-- ko -->
<string name="common_label_review_place_family">아이와 나들이하기 좋아요</string>
<!-- en -->
<string name="common_label_review_place_family">Great for outings with kids</string>
<!-- ja -->
<string name="common_label_review_place_family">子どもとのお出かけにおすすめです</string>
```

### common_label_review_place_revisit

```xml
<!-- ko -->
<string name="common_label_review_place_revisit">또 오고 싶어요</string>
<!-- en -->
<string name="common_label_review_place_revisit">Would come back</string>
<!-- ja -->
<string name="common_label_review_place_revisit">また来たいです</string>
```

### common_label_review_place_solo

```xml
<!-- ko -->
<string name="common_label_review_place_solo">혼자 가기 좋아요</string>
<!-- en -->
<string name="common_label_review_place_solo">Great for solo visits</string>
<!-- ja -->
<string name="common_label_review_place_solo">一人で行きやすいです</string>
```

### common_label_review_place_value

```xml
<!-- ko -->
<string name="common_label_review_place_value">가성비가 좋아요</string>
<!-- en -->
<string name="common_label_review_place_value">Good value</string>
<!-- ja -->
<string name="common_label_review_place_value">コスパが良いです</string>
```

### common_label_review_quick

```xml
<!-- ko -->
<string name="common_label_review_quick">응대가 빨라요</string>
<!-- en -->
<string name="common_label_review_quick">Quick service</string>
<!-- ja -->
<string name="common_label_review_quick">対応が早いです</string>
```

### common_label_review_quickcourse

```xml
<!-- ko -->
<string name="common_label_review_quickcourse">짧고 알차요</string>
<!-- en -->
<string name="common_label_review_quickcourse">Short but rewarding</string>
<!-- ja -->
<string name="common_label_review_quickcourse">短時間でも充実しています</string>
```

### common_label_review_rainyday

```xml
<!-- ko -->
<string name="common_label_review_rainyday">날씨 상관없이 즐겨요</string>
<!-- en -->
<string name="common_label_review_rainyday">Fun in any weather</string>
<!-- ja -->
<string name="common_label_review_rainyday">天候を問わず楽しめます</string>
```

### common_label_review_refreshed

```xml
<!-- ko -->
<string name="common_label_review_refreshed">몸이 개운해져요</string>
<!-- en -->
<string name="common_label_review_refreshed">Leaves you feeling refreshed</string>
<!-- ja -->
<string name="common_label_review_refreshed">体がすっきりします</string>
```

### common_label_review_relaxed

```xml
<!-- ko -->
<string name="common_label_review_relaxed">여유롭게 즐겨요</string>
<!-- en -->
<string name="common_label_review_relaxed">Enjoy at a relaxed pace</string>
<!-- ja -->
<string name="common_label_review_relaxed">ゆったり楽しめます</string>
```

### common_label_review_rest

```xml
<!-- ko -->
<string name="common_label_review_rest">조용히 쉬기 좋아요</string>
<!-- en -->
<string name="common_label_review_rest">Great for a quiet rest</string>
<!-- ja -->
<string name="common_label_review_rest">静かに休めます</string>
```

### common_label_review_result

```xml
<!-- ko -->
<string name="common_label_review_result">결과물이 만족스러워요</string>
<!-- en -->
<string name="common_label_review_result">Satisfying results</string>
<!-- ja -->
<string name="common_label_review_result">仕上がりに満足です</string>
```

### common_label_review_serene

```xml
<!-- ko -->
<string name="common_label_review_serene">고즈넉해요</string>
<!-- en -->
<string name="common_label_review_serene">Serene atmosphere</string>
<!-- ja -->
<string name="common_label_review_serene">落ち着いた雰囲気です</string>
```

### common_label_review_signature

```xml
<!-- ko -->
<string name="common_label_review_signature">시그니처 메뉴가 좋아요</string>
<!-- en -->
<string name="common_label_review_signature">Great signature items</string>
<!-- ja -->
<string name="common_label_review_signature">看板メニューが魅力的です</string>
```

### common_label_review_slowpace

```xml
<!-- ko -->
<string name="common_label_review_slowpace">천천히 걷기 좋아요</string>
<!-- en -->
<string name="common_label_review_slowpace">Great for a leisurely walk</string>
<!-- ja -->
<string name="common_label_review_slowpace">のんびり散策できます</string>
```

### common_label_review_smooth

```xml
<!-- ko -->
<string name="common_label_review_smooth">흐름이 자연스러워요</string>
<!-- en -->
<string name="common_label_review_smooth">Flows naturally</string>
<!-- ja -->
<string name="common_label_review_smooth">自然な流れで回れます</string>
```

### common_label_review_snacks

```xml
<!-- ko -->
<string name="common_label_review_snacks">안주가 맛있어요</string>
<!-- en -->
<string name="common_label_review_snacks">Delicious bar snacks</string>
<!-- ja -->
<string name="common_label_review_snacks">おつまみがおいしいです</string>
```

### common_label_review_spacious

```xml
<!-- ko -->
<string name="common_label_review_spacious">공간이 넓어요</string>
<!-- en -->
<string name="common_label_review_spacious">Spacious</string>
<!-- ja -->
<string name="common_label_review_spacious">広々としています</string>
```

### common_label_review_steep

```xml
<!-- ko -->
<string name="common_label_review_steep">언덕·계단이 많아요</string>
<!-- en -->
<string name="common_label_review_steep">Lots of hills and stairs</string>
<!-- ja -->
<string name="common_label_review_steep">坂や階段が多いです</string>
```

### common_label_review_stylish

```xml
<!-- ko -->
<string name="common_label_review_stylish">물건이 감각적이에요</string>
<!-- en -->
<string name="common_label_review_stylish">Stylish products</string>
<!-- ja -->
<string name="common_label_review_stylish">センスの良い商品があります</string>
```

### common_label_review_sunset

```xml
<!-- ko -->
<string name="common_label_review_sunset">노을이 예뻐요</string>
<!-- en -->
<string name="common_label_review_sunset">Beautiful sunset</string>
<!-- ja -->
<string name="common_label_review_sunset">夕焼けがきれいです</string>
```

### common_label_review_tasty

```xml
<!-- ko -->
<string name="common_label_review_tasty">음식이 맛있어요</string>
<!-- en -->
<string name="common_label_review_tasty">Delicious food</string>
<!-- ja -->
<string name="common_label_review_tasty">料理がおいしいです</string>
```

### common_label_review_together

```xml
<!-- ko -->
<string name="common_label_review_together">여럿이 즐기기 좋아요</string>
<!-- en -->
<string name="common_label_review_together">Great for groups</string>
<!-- ja -->
<string name="common_label_review_together">グループで楽しめます</string>
```

### common_label_review_transit

```xml
<!-- ko -->
<string name="common_label_review_transit">대중교통으로 편해요</string>
<!-- en -->
<string name="common_label_review_transit">Convenient by public transit</string>
<!-- ja -->
<string name="common_label_review_transit">公共交通で便利に回れます</string>
```

### common_label_review_variety

```xml
<!-- ko -->
<string name="common_label_review_variety">메뉴가 다양해요</string>
<!-- en -->
<string name="common_label_review_variety">Varied menu</string>
<!-- ja -->
<string name="common_label_review_variety">メニューが豊富です</string>
```

### common_label_review_view

```xml
<!-- ko -->
<string name="common_label_review_view">뷰가 좋아요</string>
<!-- en -->
<string name="common_label_review_view">Great view</string>
<!-- ja -->
<string name="common_label_review_view">眺めが良いです</string>
```

### common_label_review_viewing

```xml
<!-- ko -->
<string name="common_label_review_viewing">여유롭게 감상해요</string>
<!-- en -->
<string name="common_label_review_viewing">Enjoy at your own pace</string>
<!-- ja -->
<string name="common_label_review_viewing">ゆっくり鑑賞できます</string>
```

### common_label_review_walkable

```xml
<!-- ko -->
<string name="common_label_review_walkable">도보로 다니기 좋아요</string>
<!-- en -->
<string name="common_label_review_walkable">Easy to walk</string>
<!-- ja -->
<string name="common_label_review_walkable">徒歩で回りやすいです</string>
```

### common_label_sort_latest

```xml
<!-- ko -->
<string name="common_label_sort_latest">최신순</string>
<!-- en -->
<string name="common_label_sort_latest">Newest</string>
<!-- ja -->
<string name="common_label_sort_latest">新着順</string>
```

### common_label_sort_popular

```xml
<!-- ko -->
<string name="common_label_sort_popular">인기순</string>
<!-- en -->
<string name="common_label_sort_popular">Most popular</string>
<!-- ja -->
<string name="common_label_sort_popular">人気順</string>
```

### common_label_sort_rating

```xml
<!-- ko -->
<string name="common_label_sort_rating">높은 평점</string>
<!-- en -->
<string name="common_label_sort_rating">Highest rated</string>
<!-- ja -->
<string name="common_label_sort_rating">評価の高い順</string>
```

### common_label_sort_relevance

```xml
<!-- ko -->
<string name="common_label_sort_relevance">관련순</string>
<!-- en -->
<string name="common_label_sort_relevance">Most relevant</string>
<!-- ja -->
<string name="common_label_sort_relevance">関連度順</string>
```

### common_login_action

```xml
<!-- ko -->
<string name="common_login_action">로그인하기</string>
<!-- en -->
<string name="common_login_action">Log in</string>
<!-- ja -->
<string name="common_login_action">ログイン</string>
```

### common_login_required_description

```xml
<!-- ko -->
<string name="common_login_required_description">로그인하면 장소와 코스를 저장하고 좋아요, 후기 작성 등의 기능을 이용할 수 있어요.</string>
<!-- en -->
<string name="common_login_required_description">Log in to save places and courses, like courses, and write reviews.</string>
<!-- ja -->
<string name="common_login_required_description">ログインすると、スポットやコースの保存、いいね、口コミ投稿などを利用できます。</string>
```

### common_login_required_title

```xml
<!-- ko -->
<string name="common_login_required_title">로그인하고 계속할까요?</string>
<!-- en -->
<string name="common_login_required_title">Log in to continue?</string>
<!-- ja -->
<string name="common_login_required_title">ログインして続けますか？</string>
```

### common_no

```xml
<!-- ko -->
<string name="common_no">아니요</string>
<!-- en -->
<string name="common_no">No</string>
<!-- ja -->
<string name="common_no">いいえ</string>
```

### common_yes

```xml
<!-- ko -->
<string name="common_yes">네</string>
<!-- en -->
<string name="common_yes">Yes</string>
<!-- ja -->
<string name="common_yes">はい</string>
```

### favorite_empty

```xml
<!-- ko -->
<string name="favorite_empty">나타낼 즐겨찾기 콘텐츠가 없습니다.</string>
<!-- en -->
<string name="favorite_empty">No saved content to show.</string>
<!-- ja -->
<string name="favorite_empty">表示するお気に入りがありません。</string>
```

### favorite_register_failed

```xml
<!-- ko -->
<string name="favorite_register_failed">즐겨찾기 등록에 실패했습니다.</string>
<!-- en -->
<string name="favorite_register_failed">Couldn\'t add to favorites.</string>
<!-- ja -->
<string name="favorite_register_failed">お気に入りに追加できませんでした。</string>
```

### favorite_remove_failed

```xml
<!-- ko -->
<string name="favorite_remove_failed">즐겨찾기 해제에 실패했습니다.</string>
<!-- en -->
<string name="favorite_remove_failed">Couldn\'t remove from favorites.</string>
<!-- ja -->
<string name="favorite_remove_failed">お気に入りを解除できませんでした。</string>
```

### home_fab_create

```xml
<!-- ko -->
<string name="home_fab_create">코스 만들기 메뉴</string>
<!-- en -->
<string name="home_fab_create">Create course menu</string>
<!-- ja -->
<string name="home_fab_create">コース作成メニュー</string>
```

### home_menu_load_draft

```xml
<!-- ko -->
<string name="home_menu_load_draft">임시저장 가져오기</string>
<!-- en -->
<string name="home_menu_load_draft">Load draft</string>
<!-- ja -->
<string name="home_menu_load_draft">下書きを読み込む</string>
```

### home_menu_new_course

```xml
<!-- ko -->
<string name="home_menu_new_course">새 코스 만들기</string>
<!-- en -->
<string name="home_menu_new_course">New course</string>
<!-- ja -->
<string name="home_menu_new_course">新しいコースを作成</string>
```

### home_menu_new_plan

```xml
<!-- ko -->
<string name="home_menu_new_plan">계획하기</string>
<!-- en -->
<string name="home_menu_new_plan">Plan</string>
<!-- ja -->
<string name="home_menu_new_plan">プラン作成</string>
```

### home_nav_chat

```xml
<!-- ko -->
<string name="home_nav_chat">채팅</string>
<!-- en -->
<string name="home_nav_chat">Chat</string>
<!-- ja -->
<string name="home_nav_chat">チャット</string>
```

### home_nav_home

```xml
<!-- ko -->
<string name="home_nav_home">홈</string>
<!-- en -->
<string name="home_nav_home">Home</string>
<!-- ja -->
<string name="home_nav_home">ホーム</string>
```

### home_nav_map

```xml
<!-- ko -->
<string name="home_nav_map">지도</string>
<!-- en -->
<string name="home_nav_map">Map</string>
<!-- ja -->
<string name="home_nav_map">地図</string>
```

### home_nav_my

```xml
<!-- ko -->
<string name="home_nav_my">마이</string>
<!-- en -->
<string name="home_nav_my">My</string>
<!-- ja -->
<string name="home_nav_my">マイ</string>
```

### home_nav_saved

```xml
<!-- ko -->
<string name="home_nav_saved">저장</string>
<!-- en -->
<string name="home_nav_saved">Saved</string>
<!-- ja -->
<string name="home_nav_saved">保存</string>
```

### list_end_marker

```xml
<!-- ko -->
<string name="list_end_marker">마지막</string>
<!-- en -->
<string name="list_end_marker">End</string>
<!-- ja -->
<string name="list_end_marker">最後</string>
```

### media_default_title

```xml
<!-- ko -->
<string name="media_default_title">제목</string>
<!-- en -->
<string name="media_default_title">Title</string>
<!-- ja -->
<string name="media_default_title">タイトル</string>
```

### media_empty

```xml
<!-- ko -->
<string name="media_empty">표시할 내용이 없습니다.</string>
<!-- en -->
<string name="media_empty">No content to show.</string>
<!-- ja -->
<string name="media_empty">表示する内容がありません。</string>
```

### message_account_delete_failed

```xml
<!-- ko -->
<string name="message_account_delete_failed">회원 탈퇴에 실패했어요.</string>
<!-- en -->
<string name="message_account_delete_failed">Could not delete your account.</string>
<!-- ja -->
<string name="message_account_delete_failed">退会できませんでした。</string>
```

### message_address_load_failed

```xml
<!-- ko -->
<string name="message_address_load_failed">주소를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_address_load_failed">Could not load the address.</string>
<!-- ja -->
<string name="message_address_load_failed">住所を読み込めませんでした。</string>
```

### message_chat_active_member_count

```xml
<!-- ko -->
<string name="message_chat_active_member_count">%1$d명 대화 중</string>
<!-- en -->
<string name="message_chat_active_member_count">%1$d people chatting</string>
<!-- ja -->
<string name="message_chat_active_member_count">%1$d人がチャット中</string>
```

### message_chat_title

```xml
<!-- ko -->
<string name="message_chat_title">채팅</string>
<!-- en -->
<string name="message_chat_title">Chat</string>
<!-- ja -->
<string name="message_chat_title">チャット</string>
```

### message_comments_load_failed

```xml
<!-- ko -->
<string name="message_comments_load_failed">댓글을 불러오지 못했어요.</string>
<!-- en -->
<string name="message_comments_load_failed">Could not load comments.</string>
<!-- ja -->
<string name="message_comments_load_failed">コメントを読み込めませんでした。</string>
```

### message_comments_saved_refresh_failed

```xml
<!-- ko -->
<string name="message_comments_saved_refresh_failed">변경은 저장했지만 댓글 목록을 새로 불러오지 못했어요. 목록을 새로고침해 주세요.</string>
<!-- en -->
<string name="message_comments_saved_refresh_failed">Your changes were saved, but comments could not be reloaded. Please refresh the list.</string>
<!-- ja -->
<string name="message_comments_saved_refresh_failed">変更は保存しましたが、コメント一覧を再読み込みできませんでした。一覧を更新してください。</string>
```

### message_course_copy_load_failed

```xml
<!-- ko -->
<string name="message_course_copy_load_failed">복제할 코스를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_course_copy_load_failed">Could not load the course to copy.</string>
<!-- ja -->
<string name="message_course_copy_load_failed">コピーするコースを読み込めませんでした。</string>
```

### message_course_load_failed

```xml
<!-- ko -->
<string name="message_course_load_failed">코스를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_course_load_failed">Could not load the course.</string>
<!-- ja -->
<string name="message_course_load_failed">コースを読み込めませんでした。</string>
```

### message_course_name_required

```xml
<!-- ko -->
<string name="message_course_name_required">코스 이름을 입력해 주세요.</string>
<!-- en -->
<string name="message_course_name_required">Enter a course name.</string>
<!-- ja -->
<string name="message_course_name_required">コース名を入力してください。</string>
```

### message_course_not_found

```xml
<!-- ko -->
<string name="message_course_not_found">코스를 찾을 수 없습니다.</string>
<!-- en -->
<string name="message_course_not_found">Course not found.</string>
<!-- ja -->
<string name="message_course_not_found">コースが見つかりません。</string>
```

### message_course_photos_required

```xml
<!-- ko -->
<string name="message_course_photos_required">사진을 추가해주세요</string>
<!-- en -->
<string name="message_course_photos_required">Add photos</string>
<!-- ja -->
<string name="message_course_photos_required">写真を追加してください</string>
```

### message_course_place_count_range

```xml
<!-- ko -->
<string name="message_course_place_count_range">장소를 %1$d~%2$d곳 담아 주세요.</string>
<!-- en -->
<string name="message_course_place_count_range">Add %1$d–%2$d places.</string>
<!-- ja -->
<string name="message_course_place_count_range">場所を%1$d〜%2$dか所追加してください。</string>
```

### message_course_save_retry_later

```xml
<!-- ko -->
<string name="message_course_save_retry_later">코스 저장에 실패했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_course_save_retry_later">Could not save the course. Please try again later.</string>
<!-- ja -->
<string name="message_course_save_retry_later">コースを保存できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_course_search_failed

```xml
<!-- ko -->
<string name="message_course_search_failed">코스 검색 결과를 불러오지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_course_search_failed">Could not load course search results. Please try again.</string>
<!-- ja -->
<string name="message_course_search_failed">コースの検索結果を読み込めませんでした。もう一度お試しください。</string>
```

### message_course_thumbnail_required

```xml
<!-- ko -->
<string name="message_course_thumbnail_required">썸네일 이미지를 추가해 주세요.</string>
<!-- en -->
<string name="message_course_thumbnail_required">Add a thumbnail image.</string>
<!-- ja -->
<string name="message_course_thumbnail_required">サムネイル画像を追加してください。</string>
```

### message_courses_load_failed

```xml
<!-- ko -->
<string name="message_courses_load_failed">코스를 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_courses_load_failed">Could not load courses.</string>
<!-- ja -->
<string name="message_courses_load_failed">コースを読み込めませんでした。</string>
```

### message_delete_retry_later

```xml
<!-- ko -->
<string name="message_delete_retry_later">삭제하지 못했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_delete_retry_later">Could not delete. Please try again later.</string>
<!-- ja -->
<string name="message_delete_retry_later">削除できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_draft_content_load_failed

```xml
<!-- ko -->
<string name="message_draft_content_load_failed">임시저장한 내용을 불러오지 못했어요.</string>
<!-- en -->
<string name="message_draft_content_load_failed">Could not load the draft.</string>
<!-- ja -->
<string name="message_draft_content_load_failed">下書きを読み込めませんでした。</string>
```

### message_draft_courses_load_failed

```xml
<!-- ko -->
<string name="message_draft_courses_load_failed">임시저장한 코스를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_draft_courses_load_failed">Could not load draft courses.</string>
<!-- ja -->
<string name="message_draft_courses_load_failed">下書きのコースを読み込めませんでした。</string>
```

### message_draft_min_places

```xml
<!-- ko -->
<string name="message_draft_min_places">장소를 %1$d곳 이상 담아야 임시저장할 수 있어요.</string>
<!-- en -->
<string name="message_draft_min_places">Add at least %1$d places to save a draft.</string>
<!-- ja -->
<string name="message_draft_min_places">下書きを保存するには、場所を%1$dか所以上追加してください。</string>
```

### message_draft_save_failed

```xml
<!-- ko -->
<string name="message_draft_save_failed">임시저장에 실패했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_draft_save_failed">Could not save the draft. Please try again later.</string>
<!-- ja -->
<string name="message_draft_save_failed">下書きを保存できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_folder_create_failed

```xml
<!-- ko -->
<string name="message_folder_create_failed">폴더를 만들지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_folder_create_failed">Could not create the folder. Please try again.</string>
<!-- ja -->
<string name="message_folder_create_failed">フォルダーを作成できませんでした。もう一度お試しください。</string>
```

### message_follow_failed

```xml
<!-- ko -->
<string name="message_follow_failed">팔로우하지 못했어요.</string>
<!-- en -->
<string name="message_follow_failed">Could not follow this user.</string>
<!-- ja -->
<string name="message_follow_failed">フォローできませんでした。</string>
```

### message_following_remove_failed

```xml
<!-- ko -->
<string name="message_following_remove_failed">팔로잉을 해제하지 못했어요.</string>
<!-- en -->
<string name="message_following_remove_failed">Could not unfollow.</string>
<!-- ja -->
<string name="message_following_remove_failed">フォローを解除できませんでした。</string>
```

### message_fork_keep_min_places

```xml
<!-- ko -->
<string name="message_fork_keep_min_places">원본 장소는 %1$d곳 이상 유지해야 해요</string>
<!-- en -->
<string name="message_fork_keep_min_places">Keep at least %1$d places from the original course</string>
<!-- ja -->
<string name="message_fork_keep_min_places">元のコースの場所を%1$dか所以上残してください</string>
```

### message_fork_two_original_places

```xml
<!-- ko -->
<string name="message_fork_two_original_places">원본 장소를 2곳 이상 담아 주세요.</string>
<!-- en -->
<string name="message_fork_two_original_places">Add at least 2 places from the original course.</string>
<!-- ja -->
<string name="message_fork_two_original_places">元のコースの場所を2か所以上追加してください。</string>
```

### message_handle_allowed_characters_hint

```xml
<!-- ko -->
<string name="message_handle_allowed_characters_hint">영문 소문자, 숫자, 밑줄(_)만 사용 가능</string>
<!-- en -->
<string name="message_handle_allowed_characters_hint">Lowercase letters, numbers, and underscores (_) only</string>
<!-- ja -->
<string name="message_handle_allowed_characters_hint">半角英小文字・数字・アンダースコア（_）のみ使用可能</string>
```

### message_handle_available

```xml
<!-- ko -->
<string name="message_handle_available">사용할 수 있는 아이디예요</string>
<!-- en -->
<string name="message_handle_available">This username is available</string>
<!-- ja -->
<string name="message_handle_available">使用できるユーザーIDです</string>
```

### message_handle_check_failed

```xml
<!-- ko -->
<string name="message_handle_check_failed">확인에 실패했어요. 잠시 후 다시 시도해 주세요</string>
<!-- en -->
<string name="message_handle_check_failed">Could not check availability. Please try again later</string>
<!-- ja -->
<string name="message_handle_check_failed">確認できませんでした。しばらくしてからもう一度お試しください</string>
```

### message_handle_invalid_format

```xml
<!-- ko -->
<string name="message_handle_invalid_format">영문 소문자, 숫자, 밑줄(_)만 사용할 수 있어요</string>
<!-- en -->
<string name="message_handle_invalid_format">Use only lowercase letters, numbers, and underscores (_)</string>
<!-- ja -->
<string name="message_handle_invalid_format">半角英小文字・数字・アンダースコア（_）のみ使用できます</string>
```

### message_handle_invalid_length

```xml
<!-- ko -->
<string name="message_handle_invalid_length">3~12자 이내로 입력해 주세요</string>
<!-- en -->
<string name="message_handle_invalid_length">Enter 3–12 characters</string>
<!-- ja -->
<string name="message_handle_invalid_length">3〜12文字で入力してください</string>
```

### message_handle_length_hint

```xml
<!-- ko -->
<string name="message_handle_length_hint">3~12자 이내로 입력 가능</string>
<!-- en -->
<string name="message_handle_length_hint">3–12 characters</string>
<!-- ja -->
<string name="message_handle_length_hint">3〜12文字で入力可能</string>
```

### message_handle_taken

```xml
<!-- ko -->
<string name="message_handle_taken">이미 사용 중인 아이디예요</string>
<!-- en -->
<string name="message_handle_taken">This username is already taken</string>
<!-- ja -->
<string name="message_handle_taken">すでに使用されているユーザーIDです</string>
```

### message_kakao_login_failed

```xml
<!-- ko -->
<string name="message_kakao_login_failed">카카오 로그인을 완료하지 못했어요.</string>
<!-- en -->
<string name="message_kakao_login_failed">Could not complete Kakao sign-in.</string>
<!-- ja -->
<string name="message_kakao_login_failed">Kakaoログインを完了できませんでした。</string>
```

### message_like_failed

```xml
<!-- ko -->
<string name="message_like_failed">좋아요하지 못했어요.</string>
<!-- en -->
<string name="message_like_failed">Could not like this course.</string>
<!-- ja -->
<string name="message_like_failed">いいねできませんでした。</string>
```

### message_list_load_failed

```xml
<!-- ko -->
<string name="message_list_load_failed">목록을 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_list_load_failed">Could not load the list.</string>
<!-- ja -->
<string name="message_list_load_failed">一覧を読み込めませんでした。</string>
```

### message_load_more_failed

```xml
<!-- ko -->
<string name="message_load_more_failed">더 불러오지 못했어요.</string>
<!-- en -->
<string name="message_load_more_failed">Could not load more.</string>
<!-- ja -->
<string name="message_load_more_failed">続きを読み込めませんでした。</string>
```

### message_login_failed

```xml
<!-- ko -->
<string name="message_login_failed">로그인에 실패했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_login_failed">Could not sign in. Please try again later.</string>
<!-- ja -->
<string name="message_login_failed">ログインできませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_login_required

```xml
<!-- ko -->
<string name="message_login_required">로그인이 필요해요.</string>
<!-- en -->
<string name="message_login_required">Please sign in to continue.</string>
<!-- ja -->
<string name="message_login_required">ログインが必要です。</string>
```

### message_login_timeout

```xml
<!-- ko -->
<string name="message_login_timeout">로그인 응답이 늦어지고 있어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_login_timeout">Sign-in is taking longer than expected. Please try again.</string>
<!-- ja -->
<string name="message_login_timeout">ログインに時間がかかっています。もう一度お試しください。</string>
```

### message_map_places_retry_later

```xml
<!-- ko -->
<string name="message_map_places_retry_later">지도에서 장소를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_map_places_retry_later">Could not load places on the map. Please try again later.</string>
<!-- ja -->
<string name="message_map_places_retry_later">地図上の場所を読み込めませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_nearby_places_load_failed

```xml
<!-- ko -->
<string name="message_nearby_places_load_failed">주변 장소를 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_nearby_places_load_failed">Could not load nearby places.</string>
<!-- ja -->
<string name="message_nearby_places_load_failed">周辺の場所を読み込めませんでした。</string>
```

### message_notifications_list_changed

```xml
<!-- ko -->
<string name="message_notifications_list_changed">목록이 변경되었어요. 다시 불러와 주세요.</string>
<!-- en -->
<string name="message_notifications_list_changed">The list has changed. Please reload it.</string>
<!-- ja -->
<string name="message_notifications_list_changed">一覧が変更されました。再読み込みしてください。</string>
```

### message_notifications_load_failed

```xml
<!-- ko -->
<string name="message_notifications_load_failed">알림을 불러오지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_notifications_load_failed">Could not load notifications. Please try again.</string>
<!-- ja -->
<string name="message_notifications_load_failed">通知を読み込めませんでした。もう一度お試しください。</string>
```

### message_notifications_load_more_failed

```xml
<!-- ko -->
<string name="message_notifications_load_more_failed">다음 알림을 불러오지 못했어요.</string>
<!-- en -->
<string name="message_notifications_load_more_failed">Could not load more notifications.</string>
<!-- ja -->
<string name="message_notifications_load_more_failed">続きの通知を読み込めませんでした。</string>
```

### message_notifications_read_state_failed

```xml
<!-- ko -->
<string name="message_notifications_read_state_failed">읽음 상태를 저장하지 못했어요.</string>
<!-- en -->
<string name="message_notifications_read_state_failed">Could not save the read status.</string>
<!-- ja -->
<string name="message_notifications_read_state_failed">既読状態を保存できませんでした。</string>
```

### message_place_save_auth_failed

```xml
<!-- ko -->
<string name="message_place_save_auth_failed">장소 저장을 변경하지 못했어요. 로그인 상태를 확인해 주세요.</string>
<!-- en -->
<string name="message_place_save_auth_failed">Could not update the saved place. Please check that you are signed in.</string>
<!-- ja -->
<string name="message_place_save_auth_failed">場所の保存状態を変更できませんでした。ログイン状態を確認してください。</string>
```

### message_place_search_failed

```xml
<!-- ko -->
<string name="message_place_search_failed">장소 검색 결과를 불러오지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_place_search_failed">Could not load place search results. Please try again.</string>
<!-- ja -->
<string name="message_place_search_failed">場所の検索結果を読み込めませんでした。もう一度お試しください。</string>
```

### message_place_suggestions_load_failed

```xml
<!-- ko -->
<string name="message_place_suggestions_load_failed">장소 제안을 불러오지 못했어요. 다시 입력해 주세요.</string>
<!-- en -->
<string name="message_place_suggestions_load_failed">Could not load place suggestions. Please enter your search again.</string>
<!-- ja -->
<string name="message_place_suggestions_load_failed">場所の候補を読み込めませんでした。検索語をもう一度入力してください。</string>
```

### message_places_load_failed

```xml
<!-- ko -->
<string name="message_places_load_failed">장소를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_places_load_failed">Could not load places.</string>
<!-- ja -->
<string name="message_places_load_failed">場所を読み込めませんでした。</string>
```

### message_plan_date_format

```xml
<!-- ko -->
<string name="message_plan_date_format">날짜를 yyyy-MM-dd 형식으로 입력해 주세요.</string>
<!-- en -->
<string name="message_plan_date_format">Enter the date in yyyy-MM-dd format.</string>
<!-- ja -->
<string name="message_plan_date_format">日付はyyyy-MM-dd形式で入力してください。</string>
```

### message_plan_delete_failed

```xml
<!-- ko -->
<string name="message_plan_delete_failed">계획을 삭제하지 못했어요.</string>
<!-- en -->
<string name="message_plan_delete_failed">Could not delete the plan.</string>
<!-- ja -->
<string name="message_plan_delete_failed">プランを削除できませんでした。</string>
```

### message_plan_edit_failed

```xml
<!-- ko -->
<string name="message_plan_edit_failed">계획을 수정하지 못했어요.</string>
<!-- en -->
<string name="message_plan_edit_failed">Could not update the plan.</string>
<!-- ja -->
<string name="message_plan_edit_failed">プランを変更できませんでした。</string>
```

### message_plan_load_retry

```xml
<!-- ko -->
<string name="message_plan_load_retry">계획을 불러오지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_plan_load_retry">Could not load the plan. Please try again.</string>
<!-- ja -->
<string name="message_plan_load_retry">プランを読み込めませんでした。もう一度お試しください。</string>
```

### message_plan_memo_max_length

```xml
<!-- ko -->
<string name="message_plan_memo_max_length">메모는 500자까지 입력해 주세요.</string>
<!-- en -->
<string name="message_plan_memo_max_length">Enter a memo of up to 500 characters.</string>
<!-- ja -->
<string name="message_plan_memo_max_length">メモは500文字以内で入力してください。</string>
```

### message_plan_save_check_route

```xml
<!-- ko -->
<string name="message_plan_save_check_route">계획 저장에 실패했어요. 장소와 도보 경로를 확인한 뒤 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_plan_save_check_route">Could not save the plan. Check the places and walking routes, then try again.</string>
<!-- ja -->
<string name="message_plan_save_check_route">プランを保存できませんでした。場所と徒歩ルートを確認してから、もう一度お試しください。</string>
```

### message_plan_title_max_length

```xml
<!-- ko -->
<string name="message_plan_title_max_length">계획 제목은 200자까지 입력해 주세요.</string>
<!-- en -->
<string name="message_plan_title_max_length">Enter a plan title of up to 200 characters.</string>
<!-- ja -->
<string name="message_plan_title_max_length">プランのタイトルは200文字以内で入力してください。</string>
```

### message_plan_untitled

```xml
<!-- ko -->
<string name="message_plan_untitled">제목 없는 계획</string>
<!-- en -->
<string name="message_plan_untitled">Untitled plan</string>
<!-- ja -->
<string name="message_plan_untitled">無題のプラン</string>
```

### message_profile_load_failed

```xml
<!-- ko -->
<string name="message_profile_load_failed">프로필을 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_profile_load_failed">Could not load the profile.</string>
<!-- ja -->
<string name="message_profile_load_failed">プロフィールを読み込めませんでした。</string>
```

### message_profile_save_retry_later

```xml
<!-- ko -->
<string name="message_profile_save_retry_later">저장에 실패했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_profile_save_retry_later">Could not save. Please try again later.</string>
<!-- ja -->
<string name="message_profile_save_retry_later">保存できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_provider_apple

```xml
<!-- ko -->
<string name="message_provider_apple">애플</string>
<!-- en -->
<string name="message_provider_apple">Apple</string>
<!-- ja -->
<string name="message_provider_apple">Apple</string>
```

### message_provider_google

```xml
<!-- ko -->
<string name="message_provider_google">구글</string>
<!-- en -->
<string name="message_provider_google">Google</string>
<!-- ja -->
<string name="message_provider_google">Google</string>
```

### message_provider_kakao

```xml
<!-- ko -->
<string name="message_provider_kakao">카카오</string>
<!-- en -->
<string name="message_provider_kakao">Kakao</string>
<!-- ja -->
<string name="message_provider_kakao">Kakao</string>
```

### message_provider_login_failed

```xml
<!-- ko -->
<string name="message_provider_login_failed">%1$s 로그인을 완료하지 못했어요.</string>
<!-- en -->
<string name="message_provider_login_failed">Could not complete %1$s sign-in.</string>
<!-- ja -->
<string name="message_provider_login_failed">%1$sログインを完了できませんでした。</string>
```

### message_provider_login_unavailable

```xml
<!-- ko -->
<string name="message_provider_login_unavailable">%1$s 로그인이 아직 준비되지 않았어요.</string>
<!-- en -->
<string name="message_provider_login_unavailable">%1$s sign-in is not available yet.</string>
<!-- ja -->
<string name="message_provider_login_unavailable">%1$sログインはまだ利用できません。</string>
```

### message_provider_naver

```xml
<!-- ko -->
<string name="message_provider_naver">네이버</string>
<!-- en -->
<string name="message_provider_naver">Naver</string>
<!-- ja -->
<string name="message_provider_naver">NAVER</string>
```

### message_report_submit_failed

```xml
<!-- ko -->
<string name="message_report_submit_failed">신고하지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_report_submit_failed">Could not submit the report. Please try again.</string>
<!-- ja -->
<string name="message_report_submit_failed">通報できませんでした。もう一度お試しください。</string>
```

### message_request_retry_later

```xml
<!-- ko -->
<string name="message_request_retry_later">요청을 처리하지 못했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_request_retry_later">Could not process the request. Please try again later.</string>
<!-- ja -->
<string name="message_request_retry_later">リクエストを処理できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_retry_later

```xml
<!-- ko -->
<string name="message_retry_later">잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_retry_later">Please try again later.</string>
<!-- ja -->
<string name="message_retry_later">しばらくしてからもう一度お試しください。</string>
```

### message_review_already_deleted

```xml
<!-- ko -->
<string name="message_review_already_deleted">이미 삭제된 후기예요.</string>
<!-- en -->
<string name="message_review_already_deleted">This review has already been deleted.</string>
<!-- ja -->
<string name="message_review_already_deleted">このレビューはすでに削除されています。</string>
```

### message_review_already_written

```xml
<!-- ko -->
<string name="message_review_already_written">이미 후기를 남긴 곳이에요.</string>
<!-- en -->
<string name="message_review_already_written">You have already left a review here.</string>
<!-- ja -->
<string name="message_review_already_written">ここにはすでにレビューを投稿しています。</string>
```

### message_review_delete_failed

```xml
<!-- ko -->
<string name="message_review_delete_failed">후기를 삭제하지 못했어요.</string>
<!-- en -->
<string name="message_review_delete_failed">Could not delete the review.</string>
<!-- ja -->
<string name="message_review_delete_failed">レビューを削除できませんでした。</string>
```

### message_review_deleted

```xml
<!-- ko -->
<string name="message_review_deleted">후기를 삭제했어요.</string>
<!-- en -->
<string name="message_review_deleted">Review deleted.</string>
<!-- ja -->
<string name="message_review_deleted">レビューを削除しました。</string>
```

### message_review_submit_failed

```xml
<!-- ko -->
<string name="message_review_submit_failed">후기를 등록하지 못했어요.</string>
<!-- en -->
<string name="message_review_submit_failed">Could not submit the review.</string>
<!-- ja -->
<string name="message_review_submit_failed">レビューを投稿できませんでした。</string>
```

### message_reviews_load_failed

```xml
<!-- ko -->
<string name="message_reviews_load_failed">후기를 불러오지 못했어요.</string>
<!-- en -->
<string name="message_reviews_load_failed">Could not load reviews.</string>
<!-- ja -->
<string name="message_reviews_load_failed">レビューを読み込めませんでした。</string>
```

### message_reviews_load_more_failed

```xml
<!-- ko -->
<string name="message_reviews_load_more_failed">후기를 더 불러오지 못했어요.</string>
<!-- en -->
<string name="message_reviews_load_more_failed">Could not load more reviews.</string>
<!-- ja -->
<string name="message_reviews_load_more_failed">続きのレビューを読み込めませんでした。</string>
```

### message_save_change_failed

```xml
<!-- ko -->
<string name="message_save_change_failed">저장을 변경하지 못했어요. 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_save_change_failed">Could not update the save status. Please try again.</string>
<!-- ja -->
<string name="message_save_change_failed">保存状態を変更できませんでした。もう一度お試しください。</string>
```

### message_save_failed

```xml
<!-- ko -->
<string name="message_save_failed">저장하지 못했어요.</string>
<!-- en -->
<string name="message_save_failed">Could not save.</string>
<!-- ja -->
<string name="message_save_failed">保存できませんでした。</string>
```

### message_save_retry_later

```xml
<!-- ko -->
<string name="message_save_retry_later">저장하지 못했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_save_retry_later">Could not save. Please try again later.</string>
<!-- ja -->
<string name="message_save_retry_later">保存できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_saved_courses_load_failed

```xml
<!-- ko -->
<string name="message_saved_courses_load_failed">저장한 코스를 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_saved_courses_load_failed">Could not load saved courses.</string>
<!-- ja -->
<string name="message_saved_courses_load_failed">保存したコースを読み込めませんでした。</string>
```

### message_saved_places_load_failed

```xml
<!-- ko -->
<string name="message_saved_places_load_failed">저장한 장소를 불러오지 못했습니다.</string>
<!-- en -->
<string name="message_saved_places_load_failed">Could not load saved places.</string>
<!-- ja -->
<string name="message_saved_places_load_failed">保存した場所を読み込めませんでした。</string>
```

### message_signup_failed

```xml
<!-- ko -->
<string name="message_signup_failed">회원가입에 실패했어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="message_signup_failed">Could not create your account. Please try again later.</string>
<!-- ja -->
<string name="message_signup_failed">会員登録できませんでした。しばらくしてからもう一度お試しください。</string>
```

### message_signup_terms_required

```xml
<!-- ko -->
<string name="message_signup_terms_required">필수 약관에 동의해 주세요.</string>
<!-- en -->
<string name="message_signup_terms_required">Please agree to the required terms.</string>
<!-- ja -->
<string name="message_signup_terms_required">必須の利用規約に同意してください。</string>
```

### message_time_just_now

```xml
<!-- ko -->
<string name="message_time_just_now">방금</string>
<!-- en -->
<string name="message_time_just_now">Just now</string>
<!-- ja -->
<string name="message_time_just_now">たった今</string>
```

### message_unfollow_failed

```xml
<!-- ko -->
<string name="message_unfollow_failed">팔로우를 해제하지 못했어요.</string>
<!-- en -->
<string name="message_unfollow_failed">Could not unfollow this user.</string>
<!-- ja -->
<string name="message_unfollow_failed">フォローを解除できませんでした。</string>
```

### message_unlike_failed

```xml
<!-- ko -->
<string name="message_unlike_failed">좋아요를 취소하지 못했어요.</string>
<!-- en -->
<string name="message_unlike_failed">Could not remove your like.</string>
<!-- ja -->
<string name="message_unlike_failed">いいねを取り消せませんでした。</string>
```

### message_unsave_failed

```xml
<!-- ko -->
<string name="message_unsave_failed">저장을 취소하지 못했어요.</string>
<!-- en -->
<string name="message_unsave_failed">Could not remove from saved items.</string>
<!-- ja -->
<string name="message_unsave_failed">保存を解除できませんでした。</string>
```

### message_user_not_found

```xml
<!-- ko -->
<string name="message_user_not_found">사용자를 찾을 수 없습니다.</string>
<!-- en -->
<string name="message_user_not_found">User not found.</string>
<!-- ja -->
<string name="message_user_not_found">ユーザーが見つかりません。</string>
```

### message_version_build_number

```xml
<!-- ko -->
<string name="message_version_build_number">빌드 %1$d</string>
<!-- en -->
<string name="message_version_build_number">Build %1$d</string>
<!-- ja -->
<string name="message_version_build_number">ビルド %1$d</string>
```

### message_walking_minutes

```xml
<!-- ko -->
<string name="message_walking_minutes">도보 %1$d분</string>
<!-- en -->
<string name="message_walking_minutes">%1$d min walk</string>
<!-- ja -->
<string name="message_walking_minutes">徒歩%1$d分</string>
```

### message_walking_unreachable

```xml
<!-- ko -->
<string name="message_walking_unreachable">걸어갈 수 없는 거리</string>
<!-- en -->
<string name="message_walking_unreachable">Not reachable on foot</string>
<!-- ja -->
<string name="message_walking_unreachable">徒歩では移動できません</string>
```

### nav_back_button

```xml
<!-- ko -->
<string name="nav_back_button">이전으로</string>
<!-- en -->
<string name="nav_back_button">Back</string>
<!-- ja -->
<string name="nav_back_button">戻る</string>
```

### nav_tab_favorite

```xml
<!-- ko -->
<string name="nav_tab_favorite">즐겨찾기</string>
<!-- en -->
<string name="nav_tab_favorite">Favorites</string>
<!-- ja -->
<string name="nav_tab_favorite">お気に入り</string>
```

### nav_tab_search

```xml
<!-- ko -->
<string name="nav_tab_search">검색</string>
<!-- en -->
<string name="nav_tab_search">Search</string>
<!-- ja -->
<string name="nav_tab_search">検索</string>
```

### rating_input_description

```xml
<!-- ko -->
<string name="rating_input_description">별점 %1$d점 주기</string>
<!-- en -->
<string name="rating_input_description">Give %1$d stars</string>
<!-- ja -->
<string name="rating_input_description">星%1$d個を付ける</string>
```

### rating_stars_description

```xml
<!-- ko -->
<string name="rating_stars_description">별점 %1$d점</string>
<!-- en -->
<string name="rating_stars_description">%1$d stars</string>
<!-- ja -->
<string name="rating_stars_description">星%1$d個</string>
```

### report_action

```xml
<!-- ko -->
<string name="report_action">신고하기</string>
<!-- en -->
<string name="report_action">Report</string>
<!-- ja -->
<string name="report_action">報告する</string>
```

### report_detail_placeholder

```xml
<!-- ko -->
<string name="report_detail_placeholder">추가 설명 (선택, 최대 500자)</string>
<!-- en -->
<string name="report_detail_placeholder">Additional details (optional, up to 500 characters)</string>
<!-- ja -->
<string name="report_detail_placeholder">追加説明（任意、最大500文字）</string>
```

### report_reason_prompt

```xml
<!-- ko -->
<string name="report_reason_prompt">신고 사유를 선택해 주세요.</string>
<!-- en -->
<string name="report_reason_prompt">Please select a reason for reporting.</string>
<!-- ja -->
<string name="report_reason_prompt">報告の理由を選択してください。</string>
```

### report_submit

```xml
<!-- ko -->
<string name="report_submit">신고 접수</string>
<!-- en -->
<string name="report_submit">Submit report</string>
<!-- ja -->
<string name="report_submit">報告を送信</string>
```

### search_clear

```xml
<!-- ko -->
<string name="search_clear">검색어 지우기</string>
<!-- en -->
<string name="search_clear">Clear search</string>
<!-- ja -->
<string name="search_clear">検索ワードを消去</string>
```

### search_failed

```xml
<!-- ko -->
<string name="search_failed">검색에 실패했습니다.</string>
<!-- en -->
<string name="search_failed">Search failed.</string>
<!-- ja -->
<string name="search_failed">検索に失敗しました。</string>
```

### search_hint

```xml
<!-- ko -->
<string name="search_hint">검색 하는 내용 입니다</string>
<!-- en -->
<string name="search_hint">Enter search terms</string>
<!-- ja -->
<string name="search_hint">検索ワードを入力</string>
```

### search_prompt

```xml
<!-- ko -->
<string name="search_prompt">검색어를 입력해 보세요.</string>
<!-- en -->
<string name="search_prompt">Try entering a search term.</string>
<!-- ja -->
<string name="search_prompt">検索ワードを入力してください。</string>
```

### taxonomy_cat_bar

```xml
<!-- ko -->
<string name="taxonomy_cat_bar">술집·바</string>
<!-- en -->
<string name="taxonomy_cat_bar">Bars &amp; Pubs</string>
<!-- ja -->
<string name="taxonomy_cat_bar">居酒屋・バー</string>
```

### taxonomy_cat_cafe

```xml
<!-- ko -->
<string name="taxonomy_cat_cafe">카페·디저트</string>
<!-- en -->
<string name="taxonomy_cat_cafe">Cafes &amp; Desserts</string>
<!-- ja -->
<string name="taxonomy_cat_cafe">カフェ・デザート</string>
```

### taxonomy_cat_culture

```xml
<!-- ko -->
<string name="taxonomy_cat_culture">문화·전시</string>
<!-- en -->
<string name="taxonomy_cat_culture">Culture &amp; Exhibitions</string>
<!-- ja -->
<string name="taxonomy_cat_culture">文化・展示</string>
```

### taxonomy_cat_entertainment

```xml
<!-- ko -->
<string name="taxonomy_cat_entertainment">여가·놀거리</string>
<!-- en -->
<string name="taxonomy_cat_entertainment">Leisure &amp; Fun</string>
<!-- ja -->
<string name="taxonomy_cat_entertainment">レジャー・遊び</string>
```

### taxonomy_cat_etc

```xml
<!-- ko -->
<string name="taxonomy_cat_etc">기타</string>
<!-- en -->
<string name="taxonomy_cat_etc">Other</string>
<!-- ja -->
<string name="taxonomy_cat_etc">その他</string>
```

### taxonomy_cat_experience

```xml
<!-- ko -->
<string name="taxonomy_cat_experience">체험·클래스</string>
<!-- en -->
<string name="taxonomy_cat_experience">Experiences &amp; Classes</string>
<!-- ja -->
<string name="taxonomy_cat_experience">体験・クラス</string>
```

### taxonomy_cat_landmark

```xml
<!-- ko -->
<string name="taxonomy_cat_landmark">역사·명소</string>
<!-- en -->
<string name="taxonomy_cat_landmark">History &amp; Landmarks</string>
<!-- ja -->
<string name="taxonomy_cat_landmark">歴史・名所</string>
```

### taxonomy_cat_nature

```xml
<!-- ko -->
<string name="taxonomy_cat_nature">자연·아웃도어</string>
<!-- en -->
<string name="taxonomy_cat_nature">Nature &amp; Outdoors</string>
<!-- ja -->
<string name="taxonomy_cat_nature">自然・アウトドア</string>
```

### taxonomy_cat_restaurant

```xml
<!-- ko -->
<string name="taxonomy_cat_restaurant">음식점</string>
<!-- en -->
<string name="taxonomy_cat_restaurant">Restaurants</string>
<!-- ja -->
<string name="taxonomy_cat_restaurant">飲食店</string>
```

### taxonomy_cat_shopping

```xml
<!-- ko -->
<string name="taxonomy_cat_shopping">쇼핑·상점</string>
<!-- en -->
<string name="taxonomy_cat_shopping">Shopping &amp; Stores</string>
<!-- ja -->
<string name="taxonomy_cat_shopping">ショッピング・店舗</string>
```

### taxonomy_cat_wellness

```xml
<!-- ko -->
<string name="taxonomy_cat_wellness">웰니스·힐링</string>
<!-- en -->
<string name="taxonomy_cat_wellness">Wellness &amp; Healing</string>
<!-- ja -->
<string name="taxonomy_cat_wellness">ウェルネス・癒やし</string>
```

### taxonomy_detail_accommodation

```xml
<!-- ko -->
<string name="taxonomy_detail_accommodation">숙박</string>
<!-- en -->
<string name="taxonomy_detail_accommodation">Accommodation</string>
<!-- ja -->
<string name="taxonomy_detail_accommodation">宿泊</string>
```

### taxonomy_detail_accommodation_facility

```xml
<!-- ko -->
<string name="taxonomy_detail_accommodation_facility">숙박부속시설</string>
<!-- en -->
<string name="taxonomy_detail_accommodation_facility">Accommodation Facility</string>
<!-- ja -->
<string name="taxonomy_detail_accommodation_facility">宿泊附属施設</string>
```

### taxonomy_detail_aquarium

```xml
<!-- ko -->
<string name="taxonomy_detail_aquarium">아쿠아리움</string>
<!-- en -->
<string name="taxonomy_detail_aquarium">Aquarium</string>
<!-- ja -->
<string name="taxonomy_detail_aquarium">水族館</string>
```

### taxonomy_detail_art_craft

```xml
<!-- ko -->
<string name="taxonomy_detail_art_craft">미술·공예</string>
<!-- en -->
<string name="taxonomy_detail_art_craft">Arts &amp; Crafts</string>
<!-- ja -->
<string name="taxonomy_detail_art_craft">美術・工芸</string>
```

### taxonomy_detail_bakery

```xml
<!-- ko -->
<string name="taxonomy_detail_bakery">제과·베이커리</string>
<!-- en -->
<string name="taxonomy_detail_bakery">Bakery</string>
<!-- ja -->
<string name="taxonomy_detail_bakery">製菓・ベーカリー</string>
```

### taxonomy_detail_bathhouse_sauna

```xml
<!-- ko -->
<string name="taxonomy_detail_bathhouse_sauna">목욕탕·사우나</string>
<!-- en -->
<string name="taxonomy_detail_bathhouse_sauna">Bathhouse &amp; Sauna</string>
<!-- ja -->
<string name="taxonomy_detail_bathhouse_sauna">銭湯・サウナ</string>
```

### taxonomy_detail_beauty_salon

```xml
<!-- ko -->
<string name="taxonomy_detail_beauty_salon">미용</string>
<!-- en -->
<string name="taxonomy_detail_beauty_salon">Beauty Salon</string>
<!-- ja -->
<string name="taxonomy_detail_beauty_salon">美容</string>
```

### taxonomy_detail_bookstore

```xml
<!-- ko -->
<string name="taxonomy_detail_bookstore">도서</string>
<!-- en -->
<string name="taxonomy_detail_bookstore">Bookstore</string>
<!-- ja -->
<string name="taxonomy_detail_bookstore">書籍</string>
```

### taxonomy_detail_botanical_garden

```xml
<!-- ko -->
<string name="taxonomy_detail_botanical_garden">수목원·식물원</string>
<!-- en -->
<string name="taxonomy_detail_botanical_garden">Botanical Garden</string>
<!-- ja -->
<string name="taxonomy_detail_botanical_garden">樹木園・植物園</string>
```

### taxonomy_detail_bridge

```xml
<!-- ko -->
<string name="taxonomy_detail_bridge">교량·다리</string>
<!-- en -->
<string name="taxonomy_detail_bridge">Bridge</string>
<!-- ja -->
<string name="taxonomy_detail_bridge">橋</string>
```

### taxonomy_detail_buddhist_temple

```xml
<!-- ko -->
<string name="taxonomy_detail_buddhist_temple">불교</string>
<!-- en -->
<string name="taxonomy_detail_buddhist_temple">Buddhist Temple</string>
<!-- ja -->
<string name="taxonomy_detail_buddhist_temple">仏教</string>
```

### taxonomy_detail_building

```xml
<!-- ko -->
<string name="taxonomy_detail_building">빌딩</string>
<!-- en -->
<string name="taxonomy_detail_building">Building</string>
<!-- ja -->
<string name="taxonomy_detail_building">ビル</string>
```

### taxonomy_detail_cafe

```xml
<!-- ko -->
<string name="taxonomy_detail_cafe">카페</string>
<!-- en -->
<string name="taxonomy_detail_cafe">Cafe</string>
<!-- ja -->
<string name="taxonomy_detail_cafe">カフェ</string>
```

### taxonomy_detail_camping

```xml
<!-- ko -->
<string name="taxonomy_detail_camping">야영·캠핑장</string>
<!-- en -->
<string name="taxonomy_detail_camping">Campground</string>
<!-- ja -->
<string name="taxonomy_detail_camping">キャンプ場</string>
```

### taxonomy_detail_catholic_church

```xml
<!-- ko -->
<string name="taxonomy_detail_catholic_church">천주교</string>
<!-- en -->
<string name="taxonomy_detail_catholic_church">Catholic Church</string>
<!-- ja -->
<string name="taxonomy_detail_catholic_church">カトリック</string>
```

### taxonomy_detail_convenience_store

```xml
<!-- ko -->
<string name="taxonomy_detail_convenience_store">편의점</string>
<!-- en -->
<string name="taxonomy_detail_convenience_store">Convenience Store</string>
<!-- ja -->
<string name="taxonomy_detail_convenience_store">コンビニ</string>
```

### taxonomy_detail_culture_facility

```xml
<!-- ko -->
<string name="taxonomy_detail_culture_facility">문화시설</string>
<!-- en -->
<string name="taxonomy_detail_culture_facility">Cultural Facility</string>
<!-- ja -->
<string name="taxonomy_detail_culture_facility">文化施設</string>
```

### taxonomy_detail_department_store

```xml
<!-- ko -->
<string name="taxonomy_detail_department_store">백화점</string>
<!-- en -->
<string name="taxonomy_detail_department_store">Department Store</string>
<!-- ja -->
<string name="taxonomy_detail_department_store">百貨店</string>
```

### taxonomy_detail_drugstore

```xml
<!-- ko -->
<string name="taxonomy_detail_drugstore">드럭스토어</string>
<!-- en -->
<string name="taxonomy_detail_drugstore">Drugstore</string>
<!-- ja -->
<string name="taxonomy_detail_drugstore">ドラッグストア</string>
```

### taxonomy_detail_ecology_reserve

```xml
<!-- ko -->
<string name="taxonomy_detail_ecology_reserve">생태보존·서식지</string>
<!-- en -->
<string name="taxonomy_detail_ecology_reserve">Ecology Reserve</string>
<!-- ja -->
<string name="taxonomy_detail_ecology_reserve">生態保全・生息地</string>
```

### taxonomy_detail_electronics

```xml
<!-- ko -->
<string name="taxonomy_detail_electronics">전자제품판매</string>
<!-- en -->
<string name="taxonomy_detail_electronics">Electronics</string>
<!-- ja -->
<string name="taxonomy_detail_electronics">電化製品販売</string>
```

### taxonomy_detail_experience_trip

```xml
<!-- ko -->
<string name="taxonomy_detail_experience_trip">체험여행</string>
<!-- en -->
<string name="taxonomy_detail_experience_trip">Experience Trip</string>
<!-- ja -->
<string name="taxonomy_detail_experience_trip">体験旅行</string>
```

### taxonomy_detail_farm

```xml
<!-- ko -->
<string name="taxonomy_detail_farm">농장·목장</string>
<!-- en -->
<string name="taxonomy_detail_farm">Farm</string>
<!-- ja -->
<string name="taxonomy_detail_farm">農場・牧場</string>
```

### taxonomy_detail_fashion

```xml
<!-- ko -->
<string name="taxonomy_detail_fashion">패션</string>
<!-- en -->
<string name="taxonomy_detail_fashion">Fashion</string>
<!-- ja -->
<string name="taxonomy_detail_fashion">ファッション</string>
```

### taxonomy_detail_festival

```xml
<!-- ko -->
<string name="taxonomy_detail_festival">페스티벌</string>
<!-- en -->
<string name="taxonomy_detail_festival">Festival</string>
<!-- ja -->
<string name="taxonomy_detail_festival">フェスティバル</string>
```

### taxonomy_detail_florist

```xml
<!-- ko -->
<string name="taxonomy_detail_florist">꽃집·꽃배달</string>
<!-- en -->
<string name="taxonomy_detail_florist">Florist</string>
<!-- ja -->
<string name="taxonomy_detail_florist">花屋・花配達</string>
```

### taxonomy_detail_food_store

```xml
<!-- ko -->
<string name="taxonomy_detail_food_store">식품판매</string>
<!-- en -->
<string name="taxonomy_detail_food_store">Food Store</string>
<!-- ja -->
<string name="taxonomy_detail_food_store">食品販売</string>
```

### taxonomy_detail_forest

```xml
<!-- ko -->
<string name="taxonomy_detail_forest">숲</string>
<!-- en -->
<string name="taxonomy_detail_forest">Forest</string>
<!-- ja -->
<string name="taxonomy_detail_forest">森</string>
```

### taxonomy_detail_furniture

```xml
<!-- ko -->
<string name="taxonomy_detail_furniture">가구판매</string>
<!-- en -->
<string name="taxonomy_detail_furniture">Furniture</string>
<!-- ja -->
<string name="taxonomy_detail_furniture">家具販売</string>
```

### taxonomy_detail_gallery

```xml
<!-- ko -->
<string name="taxonomy_detail_gallery">화랑</string>
<!-- en -->
<string name="taxonomy_detail_gallery">Gallery</string>
<!-- ja -->
<string name="taxonomy_detail_gallery">ギャラリー</string>
```

### taxonomy_detail_harbor

```xml
<!-- ko -->
<string name="taxonomy_detail_harbor">항구·포구</string>
<!-- en -->
<string name="taxonomy_detail_harbor">Harbor</string>
<!-- ja -->
<string name="taxonomy_detail_harbor">港・漁港</string>
```

### taxonomy_detail_hiking_trail

```xml
<!-- ko -->
<string name="taxonomy_detail_hiking_trail">등산로</string>
<!-- en -->
<string name="taxonomy_detail_hiking_trail">Hiking Trail</string>
<!-- ja -->
<string name="taxonomy_detail_hiking_trail">登山道</string>
```

### taxonomy_detail_hobby_supplies

```xml
<!-- ko -->
<string name="taxonomy_detail_hobby_supplies">취미용품점</string>
<!-- en -->
<string name="taxonomy_detail_hobby_supplies">Hobby Supplies</string>
<!-- ja -->
<string name="taxonomy_detail_hobby_supplies">趣味用品店</string>
```

### taxonomy_detail_hot_spring

```xml
<!-- ko -->
<string name="taxonomy_detail_hot_spring">온천</string>
<!-- en -->
<string name="taxonomy_detail_hot_spring">Hot Spring</string>
<!-- ja -->
<string name="taxonomy_detail_hot_spring">温泉</string>
```

### taxonomy_detail_household_goods

```xml
<!-- ko -->
<string name="taxonomy_detail_household_goods">생활용품점</string>
<!-- en -->
<string name="taxonomy_detail_household_goods">Household Goods</string>
<!-- ja -->
<string name="taxonomy_detail_household_goods">生活用品店</string>
```

### taxonomy_detail_island

```xml
<!-- ko -->
<string name="taxonomy_detail_island">섬</string>
<!-- en -->
<string name="taxonomy_detail_island">Island</string>
<!-- ja -->
<string name="taxonomy_detail_island">島</string>
```

### taxonomy_detail_lake

```xml
<!-- ko -->
<string name="taxonomy_detail_lake">호수</string>
<!-- en -->
<string name="taxonomy_detail_lake">Lake</string>
<!-- ja -->
<string name="taxonomy_detail_lake">湖</string>
```

### taxonomy_detail_learning_center

```xml
<!-- ko -->
<string name="taxonomy_detail_learning_center">체험학습장</string>
<!-- en -->
<string name="taxonomy_detail_learning_center">Learning Center</string>
<!-- ja -->
<string name="taxonomy_detail_learning_center">体験学習場</string>
```

### taxonomy_detail_leisure_facility

```xml
<!-- ko -->
<string name="taxonomy_detail_leisure_facility">여가시설</string>
<!-- en -->
<string name="taxonomy_detail_leisure_facility">Leisure Facility</string>
<!-- ja -->
<string name="taxonomy_detail_leisure_facility">レジャー施設</string>
```

### taxonomy_detail_library

```xml
<!-- ko -->
<string name="taxonomy_detail_library">도서관</string>
<!-- en -->
<string name="taxonomy_detail_library">Library</string>
<!-- ja -->
<string name="taxonomy_detail_library">図書館</string>
```

### taxonomy_detail_market

```xml
<!-- ko -->
<string name="taxonomy_detail_market">시장</string>
<!-- en -->
<string name="taxonomy_detail_market">Market</string>
<!-- ja -->
<string name="taxonomy_detail_market">市場</string>
```

### taxonomy_detail_mountain

```xml
<!-- ko -->
<string name="taxonomy_detail_mountain">산</string>
<!-- en -->
<string name="taxonomy_detail_mountain">Mountain</string>
<!-- ja -->
<string name="taxonomy_detail_mountain">山</string>
```

### taxonomy_detail_needlework

```xml
<!-- ko -->
<string name="taxonomy_detail_needlework">수예·자수</string>
<!-- en -->
<string name="taxonomy_detail_needlework">Needlework</string>
<!-- ja -->
<string name="taxonomy_detail_needlework">手芸・刺繍</string>
```

### taxonomy_detail_outlet_store

```xml
<!-- ko -->
<string name="taxonomy_detail_outlet_store">상설할인매장</string>
<!-- en -->
<string name="taxonomy_detail_outlet_store">Outlet Store</string>
<!-- ja -->
<string name="taxonomy_detail_outlet_store">アウトレット</string>
```

### taxonomy_detail_overpass

```xml
<!-- ko -->
<string name="taxonomy_detail_overpass">육교</string>
<!-- en -->
<string name="taxonomy_detail_overpass">Overpass</string>
<!-- ja -->
<string name="taxonomy_detail_overpass">歩道橋</string>
```

### taxonomy_detail_park

```xml
<!-- ko -->
<string name="taxonomy_detail_park">공원</string>
<!-- en -->
<string name="taxonomy_detail_park">Park</string>
<!-- ja -->
<string name="taxonomy_detail_park">公園</string>
```

### taxonomy_detail_park_facility

```xml
<!-- ko -->
<string name="taxonomy_detail_park_facility">공원시설물</string>
<!-- en -->
<string name="taxonomy_detail_park_facility">Park Facility</string>
<!-- ja -->
<string name="taxonomy_detail_park_facility">公園施設</string>
```

### taxonomy_detail_performance

```xml
<!-- ko -->
<string name="taxonomy_detail_performance">공연</string>
<!-- en -->
<string name="taxonomy_detail_performance">Performance</string>
<!-- ja -->
<string name="taxonomy_detail_performance">公演</string>
```

### taxonomy_detail_photo_studio

```xml
<!-- ko -->
<string name="taxonomy_detail_photo_studio">사진관·포토스튜디오</string>
<!-- en -->
<string name="taxonomy_detail_photo_studio">Photo Studio</string>
<!-- ja -->
<string name="taxonomy_detail_photo_studio">写真館・フォトスタジオ</string>
```

### taxonomy_detail_pier

```xml
<!-- ko -->
<string name="taxonomy_detail_pier">선착장·항만시설</string>
<!-- en -->
<string name="taxonomy_detail_pier">Pier</string>
<!-- ja -->
<string name="taxonomy_detail_pier">船着き場・港湾施設</string>
```

### taxonomy_detail_popup_store

```xml
<!-- ko -->
<string name="taxonomy_detail_popup_store">팝업스토어</string>
<!-- en -->
<string name="taxonomy_detail_popup_store">Pop-up Store</string>
<!-- ja -->
<string name="taxonomy_detail_popup_store">ポップアップストア</string>
```

### taxonomy_detail_pottery

```xml
<!-- ko -->
<string name="taxonomy_detail_pottery">도자기</string>
<!-- en -->
<string name="taxonomy_detail_pottery">Pottery</string>
<!-- ja -->
<string name="taxonomy_detail_pottery">陶磁器</string>
```

### taxonomy_detail_protestant_church

```xml
<!-- ko -->
<string name="taxonomy_detail_protestant_church">기독교</string>
<!-- en -->
<string name="taxonomy_detail_protestant_church">Protestant Church</string>
<!-- ja -->
<string name="taxonomy_detail_protestant_church">キリスト教</string>
```

### taxonomy_detail_pub

```xml
<!-- ko -->
<string name="taxonomy_detail_pub">술집</string>
<!-- en -->
<string name="taxonomy_detail_pub">Pub</string>
<!-- ja -->
<string name="taxonomy_detail_pub">居酒屋</string>
```

### taxonomy_detail_record_store

```xml
<!-- ko -->
<string name="taxonomy_detail_record_store">음반·레코드샵</string>
<!-- en -->
<string name="taxonomy_detail_record_store">Record Store</string>
<!-- ja -->
<string name="taxonomy_detail_record_store">レコードショップ</string>
```

### taxonomy_detail_restaurant

```xml
<!-- ko -->
<string name="taxonomy_detail_restaurant">음식점</string>
<!-- en -->
<string name="taxonomy_detail_restaurant">Restaurant</string>
<!-- ja -->
<string name="taxonomy_detail_restaurant">飲食店</string>
```

### taxonomy_detail_shopping_arcade

```xml
<!-- ko -->
<string name="taxonomy_detail_shopping_arcade">상가·아케이드</string>
<!-- en -->
<string name="taxonomy_detail_shopping_arcade">Shopping Arcade</string>
<!-- ja -->
<string name="taxonomy_detail_shopping_arcade">商店街・アーケード</string>
```

### taxonomy_detail_shopping_mall

```xml
<!-- ko -->
<string name="taxonomy_detail_shopping_mall">복합쇼핑몰</string>
<!-- en -->
<string name="taxonomy_detail_shopping_mall">Shopping Mall</string>
<!-- ja -->
<string name="taxonomy_detail_shopping_mall">複合ショッピングモール</string>
```

### taxonomy_detail_souvenir

```xml
<!-- ko -->
<string name="taxonomy_detail_souvenir">기념품판매</string>
<!-- en -->
<string name="taxonomy_detail_souvenir">Souvenirs</string>
<!-- ja -->
<string name="taxonomy_detail_souvenir">お土産販売</string>
```

### taxonomy_detail_space_rental

```xml
<!-- ko -->
<string name="taxonomy_detail_space_rental">공간대여</string>
<!-- en -->
<string name="taxonomy_detail_space_rental">Space Rental</string>
<!-- ja -->
<string name="taxonomy_detail_space_rental">スペースレンタル</string>
```

### taxonomy_detail_sports_leisure

```xml
<!-- ko -->
<string name="taxonomy_detail_sports_leisure">스포츠·레저</string>
<!-- en -->
<string name="taxonomy_detail_sports_leisure">Sports &amp; Leisure</string>
<!-- ja -->
<string name="taxonomy_detail_sports_leisure">スポーツ・レジャー</string>
```

### taxonomy_detail_stationery

```xml
<!-- ko -->
<string name="taxonomy_detail_stationery">문구·사무용품</string>
<!-- en -->
<string name="taxonomy_detail_stationery">Stationery</string>
<!-- ja -->
<string name="taxonomy_detail_stationery">文房具・事務用品</string>
```

### taxonomy_detail_stream

```xml
<!-- ko -->
<string name="taxonomy_detail_stream">하천</string>
<!-- en -->
<string name="taxonomy_detail_stream">Stream</string>
<!-- ja -->
<string name="taxonomy_detail_stream">河川</string>
```

### taxonomy_detail_supermarket

```xml
<!-- ko -->
<string name="taxonomy_detail_supermarket">슈퍼마켓</string>
<!-- en -->
<string name="taxonomy_detail_supermarket">Supermarket</string>
<!-- ja -->
<string name="taxonomy_detail_supermarket">スーパーマーケット</string>
```

### taxonomy_detail_theme_park

```xml
<!-- ko -->
<string name="taxonomy_detail_theme_park">테마파크</string>
<!-- en -->
<string name="taxonomy_detail_theme_park">Theme Park</string>
<!-- ja -->
<string name="taxonomy_detail_theme_park">テーマパーク</string>
```

### taxonomy_detail_tourist_attraction

```xml
<!-- ko -->
<string name="taxonomy_detail_tourist_attraction">관광·명소</string>
<!-- en -->
<string name="taxonomy_detail_tourist_attraction">Tourist Attraction</string>
<!-- ja -->
<string name="taxonomy_detail_tourist_attraction">観光・名所</string>
```

### taxonomy_detail_tourist_facility

```xml
<!-- ko -->
<string name="taxonomy_detail_tourist_facility">관광지부속시설</string>
<!-- en -->
<string name="taxonomy_detail_tourist_facility">Tourist Facility</string>
<!-- ja -->
<string name="taxonomy_detail_tourist_facility">観光地附属施設</string>
```

### taxonomy_detail_toy

```xml
<!-- ko -->
<string name="taxonomy_detail_toy">장난감·완구</string>
<!-- en -->
<string name="taxonomy_detail_toy">Toys</string>
<!-- ja -->
<string name="taxonomy_detail_toy">おもちゃ・玩具</string>
```

### taxonomy_detail_valley

```xml
<!-- ko -->
<string name="taxonomy_detail_valley">계곡</string>
<!-- en -->
<string name="taxonomy_detail_valley">Valley</string>
<!-- ja -->
<string name="taxonomy_detail_valley">渓谷</string>
```

### taxonomy_detail_walking_trail

```xml
<!-- ko -->
<string name="taxonomy_detail_walking_trail">도보여행</string>
<!-- en -->
<string name="taxonomy_detail_walking_trail">Walking Trail</string>
<!-- ja -->
<string name="taxonomy_detail_walking_trail">ウォーキング旅行</string>
```

### taxonomy_detail_waterfall

```xml
<!-- ko -->
<string name="taxonomy_detail_waterfall">폭포</string>
<!-- en -->
<string name="taxonomy_detail_waterfall">Waterfall</string>
<!-- ja -->
<string name="taxonomy_detail_waterfall">滝</string>
```

### taxonomy_detail_yoga_pilates

```xml
<!-- ko -->
<string name="taxonomy_detail_yoga_pilates">요가·필라테스</string>
<!-- en -->
<string name="taxonomy_detail_yoga_pilates">Yoga &amp; Pilates</string>
<!-- ja -->
<string name="taxonomy_detail_yoga_pilates">ヨガ・ピラティス</string>
```

### taxonomy_detail_zoo

```xml
<!-- ko -->
<string name="taxonomy_detail_zoo">동물원</string>
<!-- en -->
<string name="taxonomy_detail_zoo">Zoo</string>
<!-- ja -->
<string name="taxonomy_detail_zoo">動物園</string>
```

### taxonomy_tag_activity

```xml
<!-- ko -->
<string name="taxonomy_tag_activity">액티비티</string>
<!-- en -->
<string name="taxonomy_tag_activity">Activities</string>
<!-- ja -->
<string name="taxonomy_tag_activity">アクティビティ</string>
```

### taxonomy_tag_cafetour

```xml
<!-- ko -->
<string name="taxonomy_tag_cafetour">카페 투어</string>
<!-- en -->
<string name="taxonomy_tag_cafetour">Cafe Tour</string>
<!-- ja -->
<string name="taxonomy_tag_cafetour">カフェ巡り</string>
```

### taxonomy_tag_culture

```xml
<!-- ko -->
<string name="taxonomy_tag_culture">문화</string>
<!-- en -->
<string name="taxonomy_tag_culture">Culture</string>
<!-- ja -->
<string name="taxonomy_tag_culture">文化</string>
```

### taxonomy_tag_date

```xml
<!-- ko -->
<string name="taxonomy_tag_date">데이트</string>
<!-- en -->
<string name="taxonomy_tag_date">Date</string>
<!-- ja -->
<string name="taxonomy_tag_date">デート</string>
```

### taxonomy_tag_family

```xml
<!-- ko -->
<string name="taxonomy_tag_family">가족 나들이</string>
<!-- en -->
<string name="taxonomy_tag_family">Family</string>
<!-- ja -->
<string name="taxonomy_tag_family">家族でおでかけ</string>
```

### taxonomy_tag_food

```xml
<!-- ko -->
<string name="taxonomy_tag_food">맛집</string>
<!-- en -->
<string name="taxonomy_tag_food">Food</string>
<!-- ja -->
<string name="taxonomy_tag_food">グルメ</string>
```

### taxonomy_tag_healing

```xml
<!-- ko -->
<string name="taxonomy_tag_healing">힐링</string>
<!-- en -->
<string name="taxonomy_tag_healing">Healing</string>
<!-- ja -->
<string name="taxonomy_tag_healing">癒やし</string>
```

### taxonomy_tag_nature

```xml
<!-- ko -->
<string name="taxonomy_tag_nature">자연</string>
<!-- en -->
<string name="taxonomy_tag_nature">Nature</string>
<!-- ja -->
<string name="taxonomy_tag_nature">自然</string>
```

### taxonomy_tag_nightview

```xml
<!-- ko -->
<string name="taxonomy_tag_nightview">야경</string>
<!-- en -->
<string name="taxonomy_tag_nightview">Night View</string>
<!-- ja -->
<string name="taxonomy_tag_nightview">夜景</string>
```

### taxonomy_tag_shopping

```xml
<!-- ko -->
<string name="taxonomy_tag_shopping">쇼핑</string>
<!-- en -->
<string name="taxonomy_tag_shopping">Shopping</string>
<!-- ja -->
<string name="taxonomy_tag_shopping">ショッピング</string>
```

### taxonomy_tag_solo

```xml
<!-- ko -->
<string name="taxonomy_tag_solo">혼놀</string>
<!-- en -->
<string name="taxonomy_tag_solo">Solo</string>
<!-- ja -->
<string name="taxonomy_tag_solo">ひとり</string>
```

### taxonomy_tag_tradition

```xml
<!-- ko -->
<string name="taxonomy_tag_tradition">역사</string>
<!-- en -->
<string name="taxonomy_tag_tradition">History</string>
<!-- ja -->
<string name="taxonomy_tag_tradition">歴史</string>
```

## common/presentation/src/main/res/values/validation_strings.xml

### message_base_error_status

```xml
<!-- ko -->
<string name="message_base_error_status">오류 상태: %1$s</string>
<!-- en -->
<string name="message_base_error_status">Error status: %1$s</string>
<!-- ja -->
<string name="message_base_error_status">エラー状態: %1$s</string>
```

### message_base_feature_unavailable

```xml
<!-- ko -->
<string name="message_base_feature_unavailable">현재 앱 버전에서는 사용할 수 없는 기능이에요.</string>
<!-- en -->
<string name="message_base_feature_unavailable">This feature is unavailable in this app version.</string>
<!-- ja -->
<string name="message_base_feature_unavailable">現在のアプリのバージョンでは利用できない機能です。</string>
```

### message_base_go_back

```xml
<!-- ko -->
<string name="message_base_go_back">돌아가기</string>
<!-- en -->
<string name="message_base_go_back">Go back</string>
<!-- ja -->
<string name="message_base_go_back">戻る</string>
```

### message_base_move_login

```xml
<!-- ko -->
<string name="message_base_move_login">로그인으로 이동</string>
<!-- en -->
<string name="message_base_move_login">Go to login</string>
<!-- ja -->
<string name="message_base_move_login">ログインへ</string>
```

### message_base_session_expired

```xml
<!-- ko -->
<string name="message_base_session_expired">세션이 만료되었어요. 다시 로그인해 주세요.</string>
<!-- en -->
<string name="message_base_session_expired">Session expired. Please log in again.</string>
<!-- ja -->
<string name="message_base_session_expired">セッションの有効期限が切れました。再度ログインしてください。</string>
```

### message_base_temporary_error

```xml
<!-- ko -->
<string name="message_base_temporary_error">일시적인 오류가 발생했어요.</string>
<!-- en -->
<string name="message_base_temporary_error">A temporary error occurred.</string>
<!-- ja -->
<string name="message_base_temporary_error">一時的なエラーが発生しました。</string>
```

### message_chat_member_count

```xml
<!-- ko -->
<string name="message_chat_member_count">%1$d명</string>
<!-- en -->
<string name="message_chat_member_count">%1$d members</string>
<!-- ja -->
<string name="message_chat_member_count">%1$d人</string>
```

### message_chat_open_to_everyone

```xml
<!-- ko -->
<string name="message_chat_open_to_everyone">동네 누구나</string>
<!-- en -->
<string name="message_chat_open_to_everyone">Everyone in the neighborhood</string>
<!-- ja -->
<string name="message_chat_open_to_everyone">近所のどなたでも</string>
```

### message_dialog_cancel

```xml
<!-- ko -->
<string name="message_dialog_cancel">취소</string>
<!-- en -->
<string name="message_dialog_cancel">Cancel</string>
<!-- ja -->
<string name="message_dialog_cancel">キャンセル</string>
```

### message_dialog_ok

```xml
<!-- ko -->
<string name="message_dialog_ok">확인</string>
<!-- en -->
<string name="message_dialog_ok">OK</string>
<!-- ja -->
<string name="message_dialog_ok">確認</string>
```

### message_report_receipt_failed

```xml
<!-- ko -->
<string name="message_report_receipt_failed">신고 접수를 확인하지 못했어요.</string>
<!-- en -->
<string name="message_report_receipt_failed">Unable to confirm receipt of your report.</string>
<!-- ja -->
<string name="message_report_receipt_failed">報告の受け付けを確認できませんでした。</string>
```

### message_report_received

```xml
<!-- ko -->
<string name="message_report_received">신고가 접수되었습니다.</string>
<!-- en -->
<string name="message_report_received">Your report has been received.</string>
<!-- ja -->
<string name="message_report_received">報告を受け付けました。</string>
```

### message_validation_comment_length

```xml
<!-- ko -->
<string name="message_validation_comment_length">댓글은 %1$d자까지 쓸 수 있어요.</string>
<!-- en -->
<string name="message_validation_comment_length">Comments can contain up to %1$d characters.</string>
<!-- ja -->
<string name="message_validation_comment_length">コメントは%1$d文字まで入力できます。</string>
```

### message_validation_comment_required

```xml
<!-- ko -->
<string name="message_validation_comment_required">댓글 내용을 입력해 주세요.</string>
<!-- en -->
<string name="message_validation_comment_required">Enter a comment.</string>
<!-- ja -->
<string name="message_validation_comment_required">コメントを入力してください。</string>
```

### message_validation_comment_target

```xml
<!-- ko -->
<string name="message_validation_comment_target">댓글을 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_comment_target">Please check the comment.</string>
<!-- ja -->
<string name="message_validation_comment_target">コメントを確認してください。</string>
```

### message_validation_course_target

```xml
<!-- ko -->
<string name="message_validation_course_target">코스를 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_course_target">Please check the course.</string>
<!-- ja -->
<string name="message_validation_course_target">コースを確認してください。</string>
```

### message_validation_folder_length

```xml
<!-- ko -->
<string name="message_validation_folder_length">폴더 이름은 %1$d자까지 쓸 수 있습니다.</string>
<!-- en -->
<string name="message_validation_folder_length">Folder names can contain up to %1$d characters.</string>
<!-- ja -->
<string name="message_validation_folder_length">フォルダ名は%1$d文字まで入力できます。</string>
```

### message_validation_folder_required

```xml
<!-- ko -->
<string name="message_validation_folder_required">폴더 이름을 입력해 주세요.</string>
<!-- en -->
<string name="message_validation_folder_required">Enter a folder name.</string>
<!-- ja -->
<string name="message_validation_folder_required">フォルダ名を入力してください。</string>
```

### message_validation_media_size

```xml
<!-- ko -->
<string name="message_validation_media_size">이미지가 너무 큽니다. %1$dMB 이하로 올려 주세요.</string>
<!-- en -->
<string name="message_validation_media_size">The image is too large. Upload an image up to %1$d MB.</string>
<!-- ja -->
<string name="message_validation_media_size">画像が大きすぎます。%1$dMB以下の画像をアップロードしてください。</string>
```

### message_validation_media_type

```xml
<!-- ko -->
<string name="message_validation_media_type">지원하지 않는 이미지 형식입니다(%1$s). JPEG·PNG·WebP 만 올릴 수 있어요.</string>
<!-- en -->
<string name="message_validation_media_type">Unsupported image format (%1$s). Upload a JPEG, PNG, or WebP image.</string>
<!-- ja -->
<string name="message_validation_media_type">未対応の画像形式です（%1$s）。JPEG・PNG・WebP画像をアップロードしてください。</string>
```

### message_validation_original_course

```xml
<!-- ko -->
<string name="message_validation_original_course">원본 코스를 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_original_course">Please check the original course.</string>
<!-- ja -->
<string name="message_validation_original_course">元のコースを確認してください。</string>
```

### message_validation_place_information

```xml
<!-- ko -->
<string name="message_validation_place_information">장소 정보를 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_place_information">Please check the place information.</string>
<!-- ja -->
<string name="message_validation_place_information">場所の情報を確認してください。</string>
```

### message_validation_place_location

```xml
<!-- ko -->
<string name="message_validation_place_location">장소의 위치를 확인할 수 없어요.</string>
<!-- en -->
<string name="message_validation_place_location">Unable to determine the place location.</string>
<!-- ja -->
<string name="message_validation_place_location">場所の位置を確認できません。</string>
```

### message_validation_place_memo_length

```xml
<!-- ko -->
<string name="message_validation_place_memo_length">장소 메모는 500자까지 입력할 수 있어요.</string>
<!-- en -->
<string name="message_validation_place_memo_length">Place notes can contain up to 500 characters.</string>
<!-- ja -->
<string name="message_validation_place_memo_length">場所のメモは500文字まで入力できます。</string>
```

### message_validation_place_save

```xml
<!-- ko -->
<string name="message_validation_place_save">저장할 장소 id 가 없습니다.</string>
<!-- en -->
<string name="message_validation_place_save">Please select a place to save.</string>
<!-- ja -->
<string name="message_validation_place_save">保存する場所を選択してください。</string>
```

### message_validation_place_target

```xml
<!-- ko -->
<string name="message_validation_place_target">장소를 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_place_target">Please check the place.</string>
<!-- ja -->
<string name="message_validation_place_target">場所を確認してください。</string>
```

### message_validation_place_visit

```xml
<!-- ko -->
<string name="message_validation_place_visit">방문 처리할 장소 id 가 없습니다.</string>
<!-- en -->
<string name="message_validation_place_visit">Please select a place to mark as visited.</string>
<!-- ja -->
<string name="message_validation_place_visit">訪問済みにする場所を選択してください。</string>
```

### message_validation_plan_date

```xml
<!-- ko -->
<string name="message_validation_plan_date">가려는 날을 yyyy-MM-dd 형식으로 입력해 주세요.</string>
<!-- en -->
<string name="message_validation_plan_date">Enter the visit date in yyyy-MM-dd format.</string>
<!-- ja -->
<string name="message_validation_plan_date">訪問日をyyyy-MM-dd形式で入力してください。</string>
```

### message_validation_plan_memo_length

```xml
<!-- ko -->
<string name="message_validation_plan_memo_length">계획 메모는 500자까지 입력할 수 있어요.</string>
<!-- en -->
<string name="message_validation_plan_memo_length">Plan notes can contain up to 500 characters.</string>
<!-- ja -->
<string name="message_validation_plan_memo_length">プランのメモは500文字まで入力できます。</string>
```

### message_validation_plan_places

```xml
<!-- ko -->
<string name="message_validation_plan_places">장소를 1곳 이상, 10곳 이하로 담아주세요.</string>
<!-- en -->
<string name="message_validation_plan_places">Add between 1 and 10 places.</string>
<!-- ja -->
<string name="message_validation_plan_places">場所を1～10か所追加してください。</string>
```

### message_validation_plan_required

```xml
<!-- ko -->
<string name="message_validation_plan_required">계획 이름을 입력해 주세요.</string>
<!-- en -->
<string name="message_validation_plan_required">Enter a plan name.</string>
<!-- ja -->
<string name="message_validation_plan_required">プラン名を入力してください。</string>
```

### message_validation_plan_target

```xml
<!-- ko -->
<string name="message_validation_plan_target">계획을 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_plan_target">Please check the plan.</string>
<!-- ja -->
<string name="message_validation_plan_target">プランを確認してください。</string>
```

### message_validation_plan_title_length

```xml
<!-- ko -->
<string name="message_validation_plan_title_length">계획 제목은 200자까지 입력할 수 있어요.</string>
<!-- en -->
<string name="message_validation_plan_title_length">Plan titles can contain up to 200 characters.</string>
<!-- ja -->
<string name="message_validation_plan_title_length">プランのタイトルは200文字まで入力できます。</string>
```

### message_validation_rating_range

```xml
<!-- ko -->
<string name="message_validation_rating_range">별점은 %1$d~%2$d 만 보낼 수 있습니다: %3$d</string>
<!-- en -->
<string name="message_validation_rating_range">The rating must be between %1$d and %2$d: %3$d</string>
<!-- ja -->
<string name="message_validation_rating_range">評価は%1$d～%2$dの範囲で指定してください: %3$d</string>
```

### message_validation_report_description

```xml
<!-- ko -->
<string name="message_validation_report_description">설명은 500자까지 입력해 주세요.</string>
<!-- en -->
<string name="message_validation_report_description">Descriptions can contain up to 500 characters.</string>
<!-- ja -->
<string name="message_validation_report_description">説明は500文字まで入力してください。</string>
```

### message_validation_report_target

```xml
<!-- ko -->
<string name="message_validation_report_target">신고할 대상을 확인할 수 없어요.</string>
<!-- en -->
<string name="message_validation_report_target">Unable to identify what you want to report.</string>
<!-- ja -->
<string name="message_validation_report_target">報告の対象を確認できません。</string>
```

### message_validation_review_delete

```xml
<!-- ko -->
<string name="message_validation_review_delete">삭제할 리뷰 id 가 없습니다.</string>
<!-- en -->
<string name="message_validation_review_delete">Please select a review to delete.</string>
<!-- ja -->
<string name="message_validation_review_delete">削除するレビューを選択してください。</string>
```

### message_validation_review_delete_place

```xml
<!-- ko -->
<string name="message_validation_review_delete_place">후기를 지울 장소 id 가 없습니다.</string>
<!-- en -->
<string name="message_validation_review_delete_place">Please select the place whose review you want to delete.</string>
<!-- ja -->
<string name="message_validation_review_delete_place">レビューを削除する場所を選択してください。</string>
```

### message_validation_review_length

```xml
<!-- ko -->
<string name="message_validation_review_length">후기는 %1$d자까지 쓸 수 있습니다.</string>
<!-- en -->
<string name="message_validation_review_length">Reviews can contain up to %1$d characters.</string>
<!-- ja -->
<string name="message_validation_review_length">レビューは%1$d文字まで入力できます。</string>
```

### message_validation_review_photos

```xml
<!-- ko -->
<string name="message_validation_review_photos">사진은 %1$d장까지 올릴 수 있습니다.</string>
<!-- en -->
<string name="message_validation_review_photos">You can upload up to %1$d photos.</string>
<!-- ja -->
<string name="message_validation_review_photos">写真は%1$d枚までアップロードできます。</string>
```

### message_validation_review_place

```xml
<!-- ko -->
<string name="message_validation_review_place">후기를 남길 장소 id 가 없습니다.</string>
<!-- en -->
<string name="message_validation_review_place">Please select a place to review.</string>
<!-- ja -->
<string name="message_validation_review_place">レビューする場所を選択してください。</string>
```

### message_validation_review_tags

```xml
<!-- ko -->
<string name="message_validation_review_tags">태그는 %1$d개까지 고를 수 있습니다.</string>
<!-- en -->
<string name="message_validation_review_tags">You can select up to %1$d tags.</string>
<!-- ja -->
<string name="message_validation_review_tags">タグは%1$d個まで選択できます。</string>
```

### message_validation_route_same_place

```xml
<!-- ko -->
<string name="message_validation_route_same_place">출발과 도착 장소가 같습니다.</string>
<!-- en -->
<string name="message_validation_route_same_place">The starting point and destination are the same.</string>
<!-- ja -->
<string name="message_validation_route_same_place">出発地と目的地が同じです。</string>
```

### message_validation_trace_target

```xml
<!-- ko -->
<string name="message_validation_trace_target">따라가기 기록을 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_trace_target">Please check the course progress record.</string>
<!-- ja -->
<string name="message_validation_trace_target">コースの進行記録を確認してください。</string>
```

### message_validation_walking_calculate

```xml
<!-- ko -->
<string name="message_validation_walking_calculate">장소 사이의 도보 소요시간을 계산해 주세요.</string>
<!-- en -->
<string name="message_validation_walking_calculate">Calculate the walking time between places.</string>
<!-- ja -->
<string name="message_validation_walking_calculate">場所間の徒歩の所要時間を計算してください。</string>
```

### message_validation_walking_duration

```xml
<!-- ko -->
<string name="message_validation_walking_duration">도보 소요시간을 확인해 주세요.</string>
<!-- en -->
<string name="message_validation_walking_duration">Please check the walking time.</string>
<!-- ja -->
<string name="message_validation_walking_duration">徒歩の所要時間を確認してください。</string>
```

### message_validation_walking_segments

```xml
<!-- ko -->
<string name="message_validation_walking_segments">도보 구간 수가 일치하지 않아요.</string>
<!-- en -->
<string name="message_validation_walking_segments">The number of walking segments does not match.</string>
<!-- ja -->
<string name="message_validation_walking_segments">徒歩区間の数が一致しません。</string>
```

## course/presentation/src/main/res/values/opening_hours.xml

### opening_hours_always_open

```xml
<!-- ko -->
<string name="opening_hours_always_open">연중무휴</string>
<!-- en -->
<string name="opening_hours_always_open">Open every day</string>
<!-- ja -->
<string name="opening_hours_always_open">年中無休</string>
```

### opening_hours_closed

```xml
<!-- ko -->
<string name="opening_hours_closed">휴무</string>
<!-- en -->
<string name="opening_hours_closed">Closed</string>
<!-- ja -->
<string name="opening_hours_closed">休業</string>
```

### opening_hours_daily

```xml
<!-- ko -->
<string name="opening_hours_daily">매일</string>
<!-- en -->
<string name="opening_hours_daily">Daily</string>
<!-- ja -->
<string name="opening_hours_daily">毎日</string>
```

### opening_hours_regular_closed

```xml
<!-- ko -->
<string name="opening_hours_regular_closed">정기휴무</string>
<!-- en -->
<string name="opening_hours_regular_closed">Regular day off</string>
<!-- ja -->
<string name="opening_hours_regular_closed">定休日</string>
```

### opening_hours_weekdays

```xml
<!-- ko -->
<string-array name="opening_hours_weekdays">
        <item>월</item>
        <item>화</item>
        <item>수</item>
        <item>목</item>
        <item>금</item>
        <item>토</item>
        <item>일</item>
    </string-array>
<!-- en -->
<string-array name="opening_hours_weekdays">
        <item>Mon</item>
        <item>Tue</item>
        <item>Wed</item>
        <item>Thu</item>
        <item>Fri</item>
        <item>Sat</item>
        <item>Sun</item>
    </string-array>
<!-- ja -->
<string-array name="opening_hours_weekdays">
        <item>月</item>
        <item>火</item>
        <item>水</item>
        <item>木</item>
        <item>金</item>
        <item>土</item>
        <item>日</item>
    </string-array>
```

## course/presentation/src/main/res/values/remaining_strings.xml

### course_remaining_anonymous

```xml
<!-- ko -->
<string name="course_remaining_anonymous">익명</string>
<!-- en -->
<string name="course_remaining_anonymous">Anonymous</string>
<!-- ja -->
<string name="course_remaining_anonymous">匿名</string>
```

### course_remaining_followers

```xml
<!-- ko -->
<string name="course_remaining_followers">%1$s 따라감</string>
<!-- en -->
<string name="course_remaining_followers">%1$s followed</string>
<!-- ja -->
<string name="course_remaining_followers">%1$s人が利用</string>
```

### course_remaining_map_unavailable

```xml
<!-- ko -->
<string name="course_remaining_map_unavailable">지도를 열 수 없어요</string>
<!-- en -->
<string name="course_remaining_map_unavailable">Unable to open the map</string>
<!-- ja -->
<string name="course_remaining_map_unavailable">地図を開けませんでした</string>
```

### course_remaining_minutes

```xml
<!-- ko -->
<plurals name="course_remaining_minutes">
        <item quantity="one">%1$d분</item>
        <item quantity="other">%1$d분</item>
    </plurals>
<!-- en -->
<plurals name="course_remaining_minutes">
        <item quantity="one">%1$d min</item>
        <item quantity="other">%1$d min</item>
    </plurals>
<!-- ja -->
<plurals name="course_remaining_minutes">
        <item quantity="one">%1$d分</item>
        <item quantity="other">%1$d分</item>
    </plurals>
```

### course_remaining_my_plan

```xml
<!-- ko -->
<string name="course_remaining_my_plan">나의 계획</string>
<!-- en -->
<string name="course_remaining_my_plan">My plan</string>
<!-- ja -->
<string name="course_remaining_my_plan">マイプラン</string>
```

### course_remaining_new_course

```xml
<!-- ko -->
<string name="course_remaining_new_course">새 코스</string>
<!-- en -->
<string name="course_remaining_new_course">New course</string>
<!-- ja -->
<string name="course_remaining_new_course">新しいコース</string>
```

### course_remaining_other

```xml
<!-- ko -->
<string name="course_remaining_other">기타</string>
<!-- en -->
<string name="course_remaining_other">Other</string>
<!-- ja -->
<string name="course_remaining_other">その他</string>
```

### course_remaining_place_unavailable

```xml
<!-- ko -->
<string name="course_remaining_place_unavailable">장소 정보를 불러오지 못했어요</string>
<!-- en -->
<string name="course_remaining_place_unavailable">Unable to load place information</string>
<!-- ja -->
<string name="course_remaining_place_unavailable">場所の情報を読み込めませんでした</string>
```

### course_remaining_places

```xml
<!-- ko -->
<plurals name="course_remaining_places">
        <item quantity="one">%1$d곳</item>
        <item quantity="other">%1$d곳</item>
    </plurals>
<!-- en -->
<plurals name="course_remaining_places">
        <item quantity="one">%1$d place</item>
        <item quantity="other">%1$d places</item>
    </plurals>
<!-- ja -->
<plurals name="course_remaining_places">
        <item quantity="one">%1$dか所</item>
        <item quantity="other">%1$dか所</item>
    </plurals>
```

### course_remaining_rating_1

```xml
<!-- ko -->
<string name="course_remaining_rating_1">별로였어요</string>
<!-- en -->
<string name="course_remaining_rating_1">Poor</string>
<!-- ja -->
<string name="course_remaining_rating_1">よくなかったです</string>
```

### course_remaining_rating_2

```xml
<!-- ko -->
<string name="course_remaining_rating_2">아쉬웠어요</string>
<!-- en -->
<string name="course_remaining_rating_2">Disappointing</string>
<!-- ja -->
<string name="course_remaining_rating_2">少し残念でした</string>
```

### course_remaining_rating_3

```xml
<!-- ko -->
<string name="course_remaining_rating_3">보통이었어요</string>
<!-- en -->
<string name="course_remaining_rating_3">Average</string>
<!-- ja -->
<string name="course_remaining_rating_3">普通でした</string>
```

### course_remaining_rating_4

```xml
<!-- ko -->
<string name="course_remaining_rating_4">좋았어요</string>
<!-- en -->
<string name="course_remaining_rating_4">Good</string>
<!-- ja -->
<string name="course_remaining_rating_4">よかったです</string>
```

### course_remaining_rating_5

```xml
<!-- ko -->
<string name="course_remaining_rating_5">최고였어요</string>
<!-- en -->
<string name="course_remaining_rating_5">Excellent</string>
<!-- ja -->
<string name="course_remaining_rating_5">最高でした</string>
```

### course_remaining_reviews

```xml
<!-- ko -->
<plurals name="course_remaining_reviews">
        <item quantity="one">%1$d개</item>
        <item quantity="other">%1$d개</item>
    </plurals>
<!-- en -->
<plurals name="course_remaining_reviews">
        <item quantity="one">%1$d review</item>
        <item quantity="other">%1$d reviews</item>
    </plurals>
<!-- ja -->
<plurals name="course_remaining_reviews">
        <item quantity="one">%1$d件</item>
        <item quantity="other">%1$d件</item>
    </plurals>
```

### course_remaining_spots

```xml
<!-- ko -->
<plurals name="course_remaining_spots">
        <item quantity="one">%1$d 스팟</item>
        <item quantity="other">%1$d 스팟</item>
    </plurals>
<!-- en -->
<plurals name="course_remaining_spots">
        <item quantity="one">%1$d spot</item>
        <item quantity="other">%1$d spots</item>
    </plurals>
<!-- ja -->
<plurals name="course_remaining_spots">
        <item quantity="one">%1$dスポット</item>
        <item quantity="other">%1$dスポット</item>
    </plurals>
```

### course_remaining_untitled_course

```xml
<!-- ko -->
<string name="course_remaining_untitled_course">제목 없는 코스</string>
<!-- en -->
<string name="course_remaining_untitled_course">Untitled course</string>
<!-- ja -->
<string name="course_remaining_untitled_course">無題のコース</string>
```

## course/presentation/src/main/res/values/strings.xml

### course_add_place_description

```xml
<!-- ko -->
<string name="course_add_place_description">찾는 장소의 이름을 입력하면\n지도에서 찾아 후보로 보여드려요.</string>
<!-- en -->
<string name="course_add_place_description">Enter the name of the place\nand we\'ll find candidates on the map.</string>
<!-- ja -->
<string name="course_add_place_description">探している場所の名前を入力すると\n地図から候補を表示します。</string>
```

### course_add_place_name_placeholder

```xml
<!-- ko -->
<string name="course_add_place_name_placeholder">예) 어니언 성수</string>
<!-- en -->
<string name="course_add_place_name_placeholder">e.g. Onion Seongsu</string>
<!-- ja -->
<string name="course_add_place_name_placeholder">例）オニオン聖水</string>
```

### course_add_place_search

```xml
<!-- ko -->
<string name="course_add_place_search">찾기</string>
<!-- en -->
<string name="course_add_place_search">Search</string>
<!-- ja -->
<string name="course_add_place_search">検索</string>
```

### course_add_place_searching

```xml
<!-- ko -->
<string name="course_add_place_searching">찾는 중…</string>
<!-- en -->
<string name="course_add_place_searching">Searching…</string>
<!-- ja -->
<string name="course_add_place_searching">検索中…</string>
```

### course_add_place_title

```xml
<!-- ko -->
<string name="course_add_place_title">새로운 위치 추가</string>
<!-- en -->
<string name="course_add_place_title">Add a new place</string>
<!-- ja -->
<string name="course_add_place_title">新しい場所を追加</string>
```

### course_add_tag_action

```xml
<!-- ko -->
<string name="course_add_tag_action">추가</string>
<!-- en -->
<string name="course_add_tag_action">Add</string>
<!-- ja -->
<string name="course_add_tag_action">追加</string>
```

### course_add_tag_hint

```xml
<!-- ko -->
<string name="course_add_tag_hint">태그 추가</string>
<!-- en -->
<string name="course_add_tag_hint">Add a tag</string>
<!-- ja -->
<string name="course_add_tag_hint">タグを追加</string>
```

### course_comment_author_fallback

```xml
<!-- ko -->
<string name="course_comment_author_fallback">작성자</string>
<!-- en -->
<string name="course_comment_author_fallback">Author</string>
<!-- ja -->
<string name="course_comment_author_fallback">投稿者</string>
```

### course_comment_author_profile_description

```xml
<!-- ko -->
<string name="course_comment_author_profile_description">%1$s 프로필 보기</string>
<!-- en -->
<string name="course_comment_author_profile_description">View %1$s\'s profile</string>
<!-- ja -->
<string name="course_comment_author_profile_description">%1$s のプロフィールを見る</string>
```

### course_comment_author_unknown

```xml
<!-- ko -->
<string name="course_comment_author_unknown">알 수 없는 사용자</string>
<!-- en -->
<string name="course_comment_author_unknown">Unknown user</string>
<!-- ja -->
<string name="course_comment_author_unknown">不明なユーザー</string>
```

### course_comment_delete_confirm

```xml
<!-- ko -->
<string name="course_comment_delete_confirm">삭제</string>
<!-- en -->
<string name="course_comment_delete_confirm">Delete</string>
<!-- ja -->
<string name="course_comment_delete_confirm">削除</string>
```

### course_comment_delete_description

```xml
<!-- ko -->
<string name="course_comment_delete_description">삭제한 댓글은 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_comment_delete_description">Deleted comments can\'t be restored.</string>
<!-- ja -->
<string name="course_comment_delete_description">削除したコメントは元に戻せません。</string>
```

### course_comment_delete_dismiss

```xml
<!-- ko -->
<string name="course_comment_delete_dismiss">취소</string>
<!-- en -->
<string name="course_comment_delete_dismiss">Cancel</string>
<!-- ja -->
<string name="course_comment_delete_dismiss">キャンセル</string>
```

### course_comment_delete_title

```xml
<!-- ko -->
<string name="course_comment_delete_title">댓글을 삭제할까요?</string>
<!-- en -->
<string name="course_comment_delete_title">Delete this comment?</string>
<!-- ja -->
<string name="course_comment_delete_title">コメントを削除しますか？</string>
```

### course_comment_edit_description

```xml
<!-- ko -->
<string name="course_comment_edit_description">댓글은 1000자까지 쓸 수 있어요.</string>
<!-- en -->
<string name="course_comment_edit_description">Comments can be up to 1,000 characters.</string>
<!-- ja -->
<string name="course_comment_edit_description">コメントは1000文字まで書けます。</string>
```

### course_comment_edit_placeholder

```xml
<!-- ko -->
<string name="course_comment_edit_placeholder">댓글 내용</string>
<!-- en -->
<string name="course_comment_edit_placeholder">Comment</string>
<!-- ja -->
<string name="course_comment_edit_placeholder">コメント内容</string>
```

### course_comment_edit_save

```xml
<!-- ko -->
<string name="course_comment_edit_save">저장</string>
<!-- en -->
<string name="course_comment_edit_save">Save</string>
<!-- ja -->
<string name="course_comment_edit_save">保存</string>
```

### course_comment_edit_title

```xml
<!-- ko -->
<string name="course_comment_edit_title">댓글 수정</string>
<!-- en -->
<string name="course_comment_edit_title">Edit comment</string>
<!-- ja -->
<string name="course_comment_edit_title">コメントを編集</string>
```

### course_comment_empty

```xml
<!-- ko -->
<string name="course_comment_empty">첫 댓글을 남겨 보세요.</string>
<!-- en -->
<string name="course_comment_empty">Be the first to comment.</string>
<!-- ja -->
<string name="course_comment_empty">最初のコメントを残してみましょう。</string>
```

### course_comment_input_placeholder

```xml
<!-- ko -->
<string name="course_comment_input_placeholder">댓글을 남겨 주세요</string>
<!-- en -->
<string name="course_comment_input_placeholder">Leave a comment</string>
<!-- ja -->
<string name="course_comment_input_placeholder">コメントを残してください</string>
```

### course_comment_login_prompt

```xml
<!-- ko -->
<string name="course_comment_login_prompt">로그인하면 댓글을 쓸 수 있어요.</string>
<!-- en -->
<string name="course_comment_login_prompt">Log in to leave a comment.</string>
<!-- ja -->
<string name="course_comment_login_prompt">ログインするとコメントを書けます。</string>
```

### course_comment_menu_delete

```xml
<!-- ko -->
<string name="course_comment_menu_delete">삭제</string>
<!-- en -->
<string name="course_comment_menu_delete">Delete</string>
<!-- ja -->
<string name="course_comment_menu_delete">削除</string>
```

### course_comment_menu_description

```xml
<!-- ko -->
<string name="course_comment_menu_description">내 댓글 메뉴</string>
<!-- en -->
<string name="course_comment_menu_description">Comment options</string>
<!-- ja -->
<string name="course_comment_menu_description">コメントメニュー</string>
```

### course_comment_menu_edit

```xml
<!-- ko -->
<string name="course_comment_menu_edit">수정</string>
<!-- en -->
<string name="course_comment_menu_edit">Edit</string>
<!-- ja -->
<string name="course_comment_menu_edit">編集</string>
```

### course_comment_retry

```xml
<!-- ko -->
<string name="course_comment_retry">다시 시도</string>
<!-- en -->
<string name="course_comment_retry">Try again</string>
<!-- ja -->
<string name="course_comment_retry">再試行</string>
```

### course_comment_submit_description

```xml
<!-- ko -->
<string name="course_comment_submit_description">댓글 등록</string>
<!-- en -->
<string name="course_comment_submit_description">Post comment</string>
<!-- ja -->
<string name="course_comment_submit_description">コメントを投稿</string>
```

### course_comment_title

```xml
<!-- ko -->
<string name="course_comment_title">댓글</string>
<!-- en -->
<string name="course_comment_title">Comments</string>
<!-- ja -->
<string name="course_comment_title">コメント</string>
```

### course_comment_title_count

```xml
<!-- ko -->
<string name="course_comment_title_count">댓글 %1$d%2$s</string>
<!-- en -->
<string name="course_comment_title_count">Comments %1$d%2$s</string>
<!-- ja -->
<string name="course_comment_title_count">コメント %1$d%2$s</string>
```

### course_common_photo_remove

```xml
<!-- ko -->
<string name="course_common_photo_remove">사진 삭제</string>
<!-- en -->
<string name="course_common_photo_remove">Remove photo</string>
<!-- ja -->
<string name="course_common_photo_remove">写真を削除</string>
```

### course_common_topbar_close

```xml
<!-- ko -->
<string name="course_common_topbar_close">코스 만들기 닫기</string>
<!-- en -->
<string name="course_common_topbar_close">Close course builder</string>
<!-- ja -->
<string name="course_common_topbar_close">コース作成を閉じる</string>
```

### course_common_topbar_save_draft

```xml
<!-- ko -->
<string name="course_common_topbar_save_draft">임시저장</string>
<!-- en -->
<string name="course_common_topbar_save_draft">Save draft</string>
<!-- ja -->
<string name="course_common_topbar_save_draft">一時保存</string>
```

### course_common_topbar_saving

```xml
<!-- ko -->
<string name="course_common_topbar_saving">저장 중…</string>
<!-- en -->
<string name="course_common_topbar_saving">Saving…</string>
<!-- ja -->
<string name="course_common_topbar_saving">保存中…</string>
```

### course_common_topbar_title

```xml
<!-- ko -->
<string name="course_common_topbar_title">새 코스 만들기</string>
<!-- en -->
<string name="course_common_topbar_title">New course</string>
<!-- ja -->
<string name="course_common_topbar_title">新しいコース</string>
```

### course_companion_card_label

```xml
<!-- ko -->
<string name="course_companion_card_label">누구와 함께했나요? (선택)</string>
<!-- en -->
<string name="course_companion_card_label">Who did you go with? (Optional)</string>
<!-- ja -->
<string name="course_companion_card_label">誰と一緒でしたか？（任意）</string>
```

### course_complete_header_desc

```xml
<!-- ko -->
<string name="course_complete_header_desc">내 코스에 저장되었어요.</string>
<!-- en -->
<string name="course_complete_header_desc">Saved to My Courses.</string>
<!-- ja -->
<string name="course_complete_header_desc">マイコースに保存しました。</string>
```

### course_complete_header_title

```xml
<!-- ko -->
<string name="course_complete_header_title">코스가 완성됐어요!</string>
<!-- en -->
<string name="course_complete_header_title">Your course is ready!</string>
<!-- ja -->
<string name="course_complete_header_title">コースが完成しました!</string>
```

### course_complete_timeline_title

```xml
<!-- ko -->
<string name="course_complete_timeline_title">코스 동선</string>
<!-- en -->
<string name="course_complete_timeline_title">Course route</string>
<!-- ja -->
<string name="course_complete_timeline_title">コースの順路</string>
```

### course_complete_view_my_courses

```xml
<!-- ko -->
<string name="course_complete_view_my_courses">내 코스에서 보기</string>
<!-- en -->
<string name="course_complete_view_my_courses">View in My Courses</string>
<!-- ja -->
<string name="course_complete_view_my_courses">マイコースで見る</string>
```

### course_create_add_place

```xml
<!-- ko -->
<string name="course_create_add_place">＋ 장소 더 담기</string>
<!-- en -->
<string name="course_create_add_place">＋ Add place</string>
<!-- ja -->
<string name="course_create_add_place">＋ 場所を追加</string>
```

### course_create_add_place_at_max

```xml
<!-- ko -->
<string name="course_create_add_place_at_max">장소는 최대 %1$d곳까지 담을 수 있어요</string>
<!-- en -->
<string name="course_create_add_place_at_max">You can add up to %1$d places</string>
<!-- ja -->
<string name="course_create_add_place_at_max">場所は最大%1$dか所まで追加できます</string>
```

### course_create_calculating_walking

```xml
<!-- ko -->
<string name="course_create_calculating_walking">도보 시간 계산 중…</string>
<!-- en -->
<string name="course_create_calculating_walking">Calculating walking time…</string>
<!-- ja -->
<string name="course_create_calculating_walking">徒歩時間を計算中…</string>
```

### course_create_draft_saved

```xml
<!-- ko -->
<string name="course_create_draft_saved">임시저장했어요</string>
<!-- en -->
<string name="course_create_draft_saved">Draft saved</string>
<!-- ja -->
<string name="course_create_draft_saved">下書きを保存しました</string>
```

### course_create_draft_saved_images_missing

```xml
<!-- ko -->
<string name="course_create_draft_saved_images_missing">사진은 저장되지 않았어요. 이어서 작성할 때 다시 올려 주세요.</string>
<!-- en -->
<string name="course_create_draft_saved_images_missing">Photos weren’t saved. Please add them again when you continue.</string>
<!-- ja -->
<string name="course_create_draft_saved_images_missing">写真は保存されませんでした。続きを書くときにもう一度アップロードしてください。</string>
```

### course_create_exit_plan_desc

```xml
<!-- ko -->
<string name="course_create_exit_plan_desc">저장하지 않은 내용은 사라져요.</string>
<!-- en -->
<string name="course_create_exit_plan_desc">Unsaved changes will be lost.</string>
<!-- ja -->
<string name="course_create_exit_plan_desc">保存していない内容は失われます。</string>
```

### course_create_exit_plan_title

```xml
<!-- ko -->
<string name="course_create_exit_plan_title">계획 작성을 그만둘까요?</string>
<!-- en -->
<string name="course_create_exit_plan_title">Stop creating this plan?</string>
<!-- ja -->
<string name="course_create_exit_plan_title">プランの作成をやめますか？</string>
```

### course_create_fork_author_fallback

```xml
<!-- ko -->
<string name="course_create_fork_author_fallback">다른 사람</string>
<!-- en -->
<string name="course_create_fork_author_fallback">Someone</string>
<!-- ja -->
<string name="course_create_fork_author_fallback">ほかの人</string>
```

### course_create_fork_banner

```xml
<!-- ko -->
<string name="course_create_fork_banner">%1$s의 코스를 복제하는 중이에요</string>
<!-- en -->
<string name="course_create_fork_banner">Copying %1$s’s course</string>
<!-- ja -->
<string name="course_create_fork_banner">%1$sさんのコースを複製しています</string>
```

### course_create_go_back

```xml
<!-- ko -->
<string name="course_create_go_back">돌아가기</string>
<!-- en -->
<string name="course_create_go_back">Go back</string>
<!-- ja -->
<string name="course_create_go_back">戻る</string>
```

### course_create_info_header_course

```xml
<!-- ko -->
<string name="course_create_info_header_course">코스 정보</string>
<!-- en -->
<string name="course_create_info_header_course">Course details</string>
<!-- ja -->
<string name="course_create_info_header_course">コース情報</string>
```

### course_create_info_header_plan

```xml
<!-- ko -->
<string name="course_create_info_header_plan">계획 정보</string>
<!-- en -->
<string name="course_create_info_header_plan">Plan details</string>
<!-- ja -->
<string name="course_create_info_header_plan">プラン情報</string>
```

### course_create_next

```xml
<!-- ko -->
<string name="course_create_next">다음</string>
<!-- en -->
<string name="course_create_next">Next</string>
<!-- ja -->
<string name="course_create_next">次へ</string>
```

### course_create_places_count_reorder

```xml
<!-- ko -->
<string name="course_create_places_count_reorder">%1$d곳 · 드래그로 순서 변경</string>
<!-- en -->
<string name="course_create_places_count_reorder">%1$d places · Drag to reorder</string>
<!-- ja -->
<string name="course_create_places_count_reorder">%1$dか所 · ドラッグで並べ替え</string>
```

### course_create_places_header

```xml
<!-- ko -->
<string name="course_create_places_header">장소 고르기</string>
<!-- en -->
<string name="course_create_places_header">Pick places</string>
<!-- ja -->
<string name="course_create_places_header">場所を選ぶ</string>
```

### course_create_plan_info_hint

```xml
<!-- ko -->
<string name="course_create_plan_info_hint">나만 볼 수 있는 계획이에요. 제목과 메모, 날짜는 비워도 괜찮아요.</string>
<!-- en -->
<string name="course_create_plan_info_hint">Only you can see this plan. Title, memo, and date are optional.</string>
<!-- ja -->
<string name="course_create_plan_info_hint">自分だけが見られるプランです。タイトル・メモ・日付は空のままでも大丈夫です。</string>
```

### course_create_plan_saved

```xml
<!-- ko -->
<string name="course_create_plan_saved">계획을 저장했어요</string>
<!-- en -->
<string name="course_create_plan_saved">Plan saved</string>
<!-- ja -->
<string name="course_create_plan_saved">プランを保存しました</string>
```

### course_create_prev

```xml
<!-- ko -->
<string name="course_create_prev">이전</string>
<!-- en -->
<string name="course_create_prev">Back</string>
<!-- ja -->
<string name="course_create_prev">戻る</string>
```

### course_create_records_header_course

```xml
<!-- ko -->
<string name="course_create_records_header_course">장소별 기록</string>
<!-- en -->
<string name="course_create_records_header_course">Records by place</string>
<!-- ja -->
<string name="course_create_records_header_course">場所ごとの記録</string>
```

### course_create_records_header_plan

```xml
<!-- ko -->
<string name="course_create_records_header_plan">장소별 메모</string>
<!-- en -->
<string name="course_create_records_header_plan">Notes by place</string>
<!-- ja -->
<string name="course_create_records_header_plan">場所ごとのメモ</string>
```

### course_create_records_hint_course

```xml
<!-- ko -->
<string name="course_create_records_hint_course">장소마다 사진 1장은 꼭 필요해요. 한마디는 남기지 않아도 괜찮아요.</string>
<!-- en -->
<string name="course_create_records_hint_course">Each place needs at least one photo. A comment is optional.</string>
<!-- ja -->
<string name="course_create_records_hint_course">場所ごとに写真が1枚は必要です。ひとことは残さなくても大丈夫です。</string>
```

### course_create_records_hint_plan

```xml
<!-- ko -->
<string name="course_create_records_hint_plan">장소별 메모는 남기지 않아도 괜찮아요.</string>
<!-- en -->
<string name="course_create_records_hint_plan">Notes for each place are optional.</string>
<!-- ja -->
<string name="course_create_records_hint_plan">場所ごとのメモは残さなくても大丈夫です。</string>
```

### course_create_retry

```xml
<!-- ko -->
<string name="course_create_retry">다시 시도</string>
<!-- en -->
<string name="course_create_retry">Try again</string>
<!-- ja -->
<string name="course_create_retry">再試行</string>
```

### course_create_save_course

```xml
<!-- ko -->
<string name="course_create_save_course">코스 저장하기</string>
<!-- en -->
<string name="course_create_save_course">Save course</string>
<!-- ja -->
<string name="course_create_save_course">コースを保存</string>
```

### course_create_save_plan

```xml
<!-- ko -->
<string name="course_create_save_plan">계획 저장하기</string>
<!-- en -->
<string name="course_create_save_plan">Save plan</string>
<!-- ja -->
<string name="course_create_save_plan">プランを保存</string>
```

### course_create_saved_images_missing

```xml
<!-- ko -->
<string name="course_create_saved_images_missing">사진은 저장되지 않았어요. 편집에서 다시 올려 주세요.</string>
<!-- en -->
<string name="course_create_saved_images_missing">Photos weren’t saved. Please add them again from editing.</string>
<!-- ja -->
<string name="course_create_saved_images_missing">写真は保存されませんでした。編集からもう一度アップロードしてください。</string>
```

### course_create_settings_header

```xml
<!-- ko -->
<string name="course_create_settings_header">코스 설정</string>
<!-- en -->
<string name="course_create_settings_header">Course settings</string>
<!-- ja -->
<string name="course_create_settings_header">コース設定</string>
```

### course_create_title_course

```xml
<!-- ko -->
<string name="course_create_title_course">새 코스 만들기</string>
<!-- en -->
<string name="course_create_title_course">New course</string>
<!-- ja -->
<string name="course_create_title_course">新しいコース</string>
```

### course_create_title_plan

```xml
<!-- ko -->
<string name="course_create_title_plan">계획하기</string>
<!-- en -->
<string name="course_create_title_plan">New plan</string>
<!-- ja -->
<string name="course_create_title_plan">プランを作成</string>
```

### course_delete_confirm_desc

```xml
<!-- ko -->
<string name="course_delete_confirm_desc">삭제한 코스는 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_delete_confirm_desc">Deleted courses can\'t be restored.</string>
<!-- ja -->
<string name="course_delete_confirm_desc">削除したコースは元に戻せません。</string>
```

### course_delete_confirm_title

```xml
<!-- ko -->
<string name="course_delete_confirm_title">코스를 삭제할까요?</string>
<!-- en -->
<string name="course_delete_confirm_title">Delete this course?</string>
<!-- ja -->
<string name="course_delete_confirm_title">コースを削除しますか？</string>
```

### course_detail_back

```xml
<!-- ko -->
<string name="course_detail_back">뒤로</string>
<!-- en -->
<string name="course_detail_back">Back</string>
<!-- ja -->
<string name="course_detail_back">戻る</string>
```

### course_detail_close

```xml
<!-- ko -->
<string name="course_detail_close">닫기</string>
<!-- en -->
<string name="course_detail_close">Close</string>
<!-- ja -->
<string name="course_detail_close">閉じる</string>
```

### course_detail_collapse

```xml
<!-- ko -->
<string name="course_detail_collapse">접기</string>
<!-- en -->
<string name="course_detail_collapse">Collapse</string>
<!-- ja -->
<string name="course_detail_collapse">折りたたむ</string>
```

### course_detail_comments

```xml
<!-- ko -->
<string name="course_detail_comments">댓글</string>
<!-- en -->
<string name="course_detail_comments">Comments</string>
<!-- ja -->
<string name="course_detail_comments">コメント</string>
```

### course_detail_error_load_failed

```xml
<!-- ko -->
<string name="course_detail_error_load_failed">코스를 불러오지 못했습니다.</string>
<!-- en -->
<string name="course_detail_error_load_failed">Couldn\'t load the course.</string>
<!-- ja -->
<string name="course_detail_error_load_failed">コースを読み込めませんでした。</string>
```

### course_detail_error_retry

```xml
<!-- ko -->
<string name="course_detail_error_retry">다시 시도</string>
<!-- en -->
<string name="course_detail_error_retry">Retry</string>
<!-- ja -->
<string name="course_detail_error_retry">再試行</string>
```

### course_detail_expand

```xml
<!-- ko -->
<string name="course_detail_expand">펼치기</string>
<!-- en -->
<string name="course_detail_expand">Expand</string>
<!-- ja -->
<string name="course_detail_expand">開く</string>
```

### course_detail_follow

```xml
<!-- ko -->
<string name="course_detail_follow">따라가기</string>
<!-- en -->
<string name="course_detail_follow">Follow course</string>
<!-- ja -->
<string name="course_detail_follow">コースをたどる</string>
```

### course_detail_follow_author

```xml
<!-- ko -->
<string name="course_detail_follow_author">팔로우</string>
<!-- en -->
<string name="course_detail_follow_author">Follow</string>
<!-- ja -->
<string name="course_detail_follow_author">フォロー</string>
```

### course_detail_following

```xml
<!-- ko -->
<string name="course_detail_following">팔로잉</string>
<!-- en -->
<string name="course_detail_following">Following</string>
<!-- ja -->
<string name="course_detail_following">フォロー中</string>
```

### course_detail_fork

```xml
<!-- ko -->
<string name="course_detail_fork">코스 복제</string>
<!-- en -->
<string name="course_detail_fork">Copy course</string>
<!-- ja -->
<string name="course_detail_fork">コースを複製</string>
```

### course_detail_forked_badge

```xml
<!-- ko -->
<string name="course_detail_forked_badge">복제한 코스</string>
<!-- en -->
<string name="course_detail_forked_badge">Copied course</string>
<!-- ja -->
<string name="course_detail_forked_badge">複製したコース</string>
```

### course_detail_hero_back

```xml
<!-- ko -->
<string name="course_detail_hero_back">뒤로가기</string>
<!-- en -->
<string name="course_detail_hero_back">Back</string>
<!-- ja -->
<string name="course_detail_hero_back">戻る</string>
```

### course_detail_like

```xml
<!-- ko -->
<string name="course_detail_like">좋아요</string>
<!-- en -->
<string name="course_detail_like">Like</string>
<!-- ja -->
<string name="course_detail_like">いいね</string>
```

### course_detail_map_section

```xml
<!-- ko -->
<string name="course_detail_map_section">코스 한눈에 보기</string>
<!-- en -->
<string name="course_detail_map_section">Course at a glance</string>
<!-- ja -->
<string name="course_detail_map_section">コースをひと目で</string>
```

### course_detail_menu_delete

```xml
<!-- ko -->
<string name="course_detail_menu_delete">코스 삭제</string>
<!-- en -->
<string name="course_detail_menu_delete">Delete course</string>
<!-- ja -->
<string name="course_detail_menu_delete">コースを削除</string>
```

### course_detail_menu_deleting

```xml
<!-- ko -->
<string name="course_detail_menu_deleting">삭제 중…</string>
<!-- en -->
<string name="course_detail_menu_deleting">Deleting…</string>
<!-- ja -->
<string name="course_detail_menu_deleting">削除中…</string>
```

### course_detail_menu_edit

```xml
<!-- ko -->
<string name="course_detail_menu_edit">코스 편집</string>
<!-- en -->
<string name="course_detail_menu_edit">Edit course</string>
<!-- ja -->
<string name="course_detail_menu_edit">コースを編集</string>
```

### course_detail_more_places

```xml
<!-- ko -->
<string name="course_detail_more_places">나머지 %1$d곳 더보기</string>
<!-- en -->
<string name="course_detail_more_places">See %1$d more places</string>
<!-- ja -->
<string name="course_detail_more_places">残り%1$dか所を見る</string>
```

### course_detail_place_count

```xml
<!-- ko -->
<string name="course_detail_place_count">%1$d곳</string>
<!-- en -->
<string name="course_detail_place_count">%1$d places</string>
<!-- ja -->
<string name="course_detail_place_count">%1$dか所</string>
```

### course_detail_place_detail

```xml
<!-- ko -->
<string name="course_detail_place_detail">장소 상세</string>
<!-- en -->
<string name="course_detail_place_detail">Place details</string>
<!-- ja -->
<string name="course_detail_place_detail">スポットの詳細</string>
```

### course_detail_places_section

```xml
<!-- ko -->
<string name="course_detail_places_section">코스 속 장소</string>
<!-- en -->
<string name="course_detail_places_section">Places in course</string>
<!-- ja -->
<string name="course_detail_places_section">コース内のスポット</string>
```

### course_detail_save

```xml
<!-- ko -->
<string name="course_detail_save">코스 저장</string>
<!-- en -->
<string name="course_detail_save">Save course</string>
<!-- ja -->
<string name="course_detail_save">コースを保存</string>
```

### course_detail_settings

```xml
<!-- ko -->
<string name="course_detail_settings">코스 설정</string>
<!-- en -->
<string name="course_detail_settings">Course settings</string>
<!-- ja -->
<string name="course_detail_settings">コース設定</string>
```

### course_detail_share

```xml
<!-- ko -->
<string name="course_detail_share">공유</string>
<!-- en -->
<string name="course_detail_share">Share</string>
<!-- ja -->
<string name="course_detail_share">共有</string>
```

### course_detail_tip_prefix

```xml
<!-- ko -->
<string name="course_detail_tip_prefix">Tip.%1$s</string>
<!-- en -->
<string name="course_detail_tip_prefix">Tip.%1$s</string>
<!-- ja -->
<string name="course_detail_tip_prefix">ヒント：%1$s</string>
```

### course_detail_unlike

```xml
<!-- ko -->
<string name="course_detail_unlike">좋아요 취소</string>
<!-- en -->
<string name="course_detail_unlike">Unlike</string>
<!-- ja -->
<string name="course_detail_unlike">いいねを取り消す</string>
```

### course_detail_unsave

```xml
<!-- ko -->
<string name="course_detail_unsave">저장 취소</string>
<!-- en -->
<string name="course_detail_unsave">Remove from saved</string>
<!-- ja -->
<string name="course_detail_unsave">保存を解除</string>
```

### course_dialog_cancel

```xml
<!-- ko -->
<string name="course_dialog_cancel">취소</string>
<!-- en -->
<string name="course_dialog_cancel">Cancel</string>
<!-- ja -->
<string name="course_dialog_cancel">キャンセル</string>
```

### course_draft_list_back

```xml
<!-- ko -->
<string name="course_draft_list_back">뒤로</string>
<!-- en -->
<string name="course_draft_list_back">Back</string>
<!-- ja -->
<string name="course_draft_list_back">戻る</string>
```

### course_draft_list_count

```xml
<!-- ko -->
<string name="course_draft_list_count">%1$d개</string>
<!-- en -->
<string name="course_draft_list_count">%1$d</string>
<!-- ja -->
<string name="course_draft_list_count">%1$d件</string>
```

### course_draft_list_delete

```xml
<!-- ko -->
<string name="course_draft_list_delete">삭제</string>
<!-- en -->
<string name="course_draft_list_delete">Delete</string>
<!-- ja -->
<string name="course_draft_list_delete">削除</string>
```

### course_draft_list_delete_description

```xml
<!-- ko -->
<string name="course_draft_list_delete_description">\"%1$s\" 은(는) 삭제하면 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_draft_list_delete_description">\"%1$s\" can\'t be restored once deleted.</string>
<!-- ja -->
<string name="course_draft_list_delete_description">「%1$s」は削除すると元に戻せません。</string>
```

### course_draft_list_delete_title

```xml
<!-- ko -->
<string name="course_draft_list_delete_title">임시저장을 삭제할까요?</string>
<!-- en -->
<string name="course_draft_list_delete_title">Delete this draft?</string>
<!-- ja -->
<string name="course_draft_list_delete_title">下書きを削除しますか？</string>
```

### course_draft_list_empty

```xml
<!-- ko -->
<string name="course_draft_list_empty">임시저장한 코스가 없어요</string>
<!-- en -->
<string name="course_draft_list_empty">No saved drafts yet</string>
<!-- ja -->
<string name="course_draft_list_empty">下書きのコースはありません</string>
```

### course_draft_list_header_desc

```xml
<!-- ko -->
<string name="course_draft_list_header_desc">저장해 둔 코스를 이어서 만들 수 있어요.</string>
<!-- en -->
<string name="course_draft_list_header_desc">Pick up where you left off on a saved course.</string>
<!-- ja -->
<string name="course_draft_list_header_desc">保存したコースの続きを作成できます。</string>
```

### course_draft_list_load_error

```xml
<!-- ko -->
<string name="course_draft_list_load_error">임시저장한 코스를 불러오지 못했어요.</string>
<!-- en -->
<string name="course_draft_list_load_error">Couldn\'t load your drafts.</string>
<!-- ja -->
<string name="course_draft_list_load_error">下書きのコースを読み込めませんでした。</string>
```

### course_draft_list_retry

```xml
<!-- ko -->
<string name="course_draft_list_retry">다시 시도</string>
<!-- en -->
<string name="course_draft_list_retry">Try again</string>
<!-- ja -->
<string name="course_draft_list_retry">再試行</string>
```

### course_draft_list_title

```xml
<!-- ko -->
<string name="course_draft_list_title">임시저장</string>
<!-- en -->
<string name="course_draft_list_title">Drafts</string>
<!-- ja -->
<string name="course_draft_list_title">下書き</string>
```

### course_edit_close

```xml
<!-- ko -->
<string name="course_edit_close">코스 편집 닫기</string>
<!-- en -->
<string name="course_edit_close">Close course edit</string>
<!-- ja -->
<string name="course_edit_close">コース編集を閉じる</string>
```

### course_edit_description_label

```xml
<!-- ko -->
<string name="course_edit_description_label">코스 소개</string>
<!-- en -->
<string name="course_edit_description_label">Course intro</string>
<!-- ja -->
<string name="course_edit_description_label">コース紹介</string>
```

### course_edit_description_placeholder

```xml
<!-- ko -->
<string name="course_edit_description_placeholder">이 코스를 소개해 주세요</string>
<!-- en -->
<string name="course_edit_description_placeholder">Introduce this course</string>
<!-- ja -->
<string name="course_edit_description_placeholder">このコースを紹介してください</string>
```

### course_edit_place_tip_notice

```xml
<!-- ko -->
<string name="course_edit_place_tip_notice">장소와 사진은 편집할 수 없어요. 한마디만 고칠 수 있어요.</string>
<!-- en -->
<string name="course_edit_place_tip_notice">Places and photos can\'t be edited. You can only change the notes.</string>
<!-- ja -->
<string name="course_edit_place_tip_notice">スポットと写真は編集できません。ひとことだけ変更できます。</string>
```

### course_edit_place_tip_placeholder

```xml
<!-- ko -->
<string name="course_edit_place_tip_placeholder">이곳에 한마디를 남겨 보세요</string>
<!-- en -->
<string name="course_edit_place_tip_placeholder">Leave a quick note here</string>
<!-- ja -->
<string name="course_edit_place_tip_placeholder">ここにひとことを残しましょう</string>
```

### course_edit_save

```xml
<!-- ko -->
<string name="course_edit_save">저장하기</string>
<!-- en -->
<string name="course_edit_save">Save</string>
<!-- ja -->
<string name="course_edit_save">保存する</string>
```

### course_edit_saving

```xml
<!-- ko -->
<string name="course_edit_saving">저장 중…</string>
<!-- en -->
<string name="course_edit_saving">Saving…</string>
<!-- ja -->
<string name="course_edit_saving">保存中…</string>
```

### course_edit_section_info

```xml
<!-- ko -->
<string name="course_edit_section_info">코스 정보</string>
<!-- en -->
<string name="course_edit_section_info">Course info</string>
<!-- ja -->
<string name="course_edit_section_info">コース情報</string>
```

### course_edit_section_place_tip

```xml
<!-- ko -->
<string name="course_edit_section_place_tip">장소별 한마디</string>
<!-- en -->
<string name="course_edit_section_place_tip">Place notes</string>
<!-- ja -->
<string name="course_edit_section_place_tip">スポットごとのひとこと</string>
```

### course_edit_section_tag

```xml
<!-- ko -->
<string name="course_edit_section_tag">태그</string>
<!-- en -->
<string name="course_edit_section_tag">Tags</string>
<!-- ja -->
<string name="course_edit_section_tag">タグ</string>
```

### course_edit_section_visibility

```xml
<!-- ko -->
<string name="course_edit_section_visibility">공개 설정</string>
<!-- en -->
<string name="course_edit_section_visibility">Visibility</string>
<!-- ja -->
<string name="course_edit_section_visibility">公開設定</string>
```

### course_edit_title_label

```xml
<!-- ko -->
<string name="course_edit_title_label">코스 이름</string>
<!-- en -->
<string name="course_edit_title_label">Course name</string>
<!-- ja -->
<string name="course_edit_title_label">コース名</string>
```

### course_edit_title_placeholder

```xml
<!-- ko -->
<string name="course_edit_title_placeholder">코스 이름을 입력해 주세요</string>
<!-- en -->
<string name="course_edit_title_placeholder">Enter a course name</string>
<!-- ja -->
<string name="course_edit_title_placeholder">コース名を入力してください</string>
```

### course_edit_top_bar_title

```xml
<!-- ko -->
<string name="course_edit_top_bar_title">코스 편집</string>
<!-- en -->
<string name="course_edit_top_bar_title">Edit course</string>
<!-- ja -->
<string name="course_edit_top_bar_title">コースを編集</string>
```

### course_edit_visibility_unknown_notice

```xml
<!-- ko -->
<string name="course_edit_visibility_unknown_notice">현재 설정을 불러오지 못했어요. 저장하면 아래에서 고른 값으로 바뀝니다.</string>
<!-- en -->
<string name="course_edit_visibility_unknown_notice">Couldn\'t load the current setting. Saving will apply the option you pick below.</string>
<!-- ja -->
<string name="course_edit_visibility_unknown_notice">現在の設定を読み込めませんでした。保存すると、下で選んだ内容に変わります。</string>
```

### course_exit_confirm_desc

```xml
<!-- ko -->
<string name="course_exit_confirm_desc">지금 그냥 나가면 변경사항이 사라져요.</string>
<!-- en -->
<string name="course_exit_confirm_desc">If you leave now, your changes will be lost.</string>
<!-- ja -->
<string name="course_exit_confirm_desc">このまま出ると変更内容が失われます。</string>
```

### course_exit_confirm_title

```xml
<!-- ko -->
<string name="course_exit_confirm_title">작성 중인 코스를 임시저장할까요?</string>
<!-- en -->
<string name="course_exit_confirm_title">Save this course as a draft?</string>
<!-- ja -->
<string name="course_exit_confirm_title">作成中のコースを下書き保存しますか？</string>
```

### course_exit_discard

```xml
<!-- ko -->
<string name="course_exit_discard">저장 안 함</string>
<!-- en -->
<string name="course_exit_discard">Don\'t save</string>
<!-- ja -->
<string name="course_exit_discard">保存しない</string>
```

### course_exit_save_and_exit

```xml
<!-- ko -->
<string name="course_exit_save_and_exit">임시저장하고 나가기</string>
<!-- en -->
<string name="course_exit_save_and_exit">Save draft and exit</string>
<!-- ja -->
<string name="course_exit_save_and_exit">下書き保存して出る</string>
```

### course_external_reviews_map_open_failed

```xml
<!-- ko -->
<string name="course_external_reviews_map_open_failed">네이버 지도를 열 수 없어요</string>
<!-- en -->
<string name="course_external_reviews_map_open_failed">Can\'t open Naver Map</string>
<!-- ja -->
<string name="course_external_reviews_map_open_failed">NAVERマップを開けませんでした</string>
```

### course_external_reviews_open_naver

```xml
<!-- ko -->
<string name="course_external_reviews_open_naver">네이버 지도에서 찾아보기</string>
<!-- en -->
<string name="course_external_reviews_open_naver">Search on Naver Map</string>
<!-- ja -->
<string name="course_external_reviews_open_naver">NAVERマップで探す</string>
```

### course_external_reviews_prompt

```xml
<!-- ko -->
<string name="course_external_reviews_prompt">다른 방문자의 후기도 궁금하다면</string>
<!-- en -->
<string name="course_external_reviews_prompt">Curious about other visitors\' reviews?</string>
<!-- ja -->
<string name="course_external_reviews_prompt">ほかの訪問者のレビューも見てみませんか？</string>
```

### course_follow_arrive

```xml
<!-- ko -->
<string name="course_follow_arrive">도착</string>
<!-- en -->
<string name="course_follow_arrive">Arrived</string>
<!-- ja -->
<string name="course_follow_arrive">到着</string>
```

### course_follow_back

```xml
<!-- ko -->
<string name="course_follow_back">뒤로</string>
<!-- en -->
<string name="course_follow_back">Back</string>
<!-- ja -->
<string name="course_follow_back">戻る</string>
```

### course_follow_done_title

```xml
<!-- ko -->
<string name="course_follow_done_title">코스 완주!\n수고했어요</string>
<!-- en -->
<string name="course_follow_done_title">Course complete!\nGreat job</string>
<!-- ja -->
<string name="course_follow_done_title">コース完走！\nお疲れさまでした</string>
```

### course_follow_end

```xml
<!-- ko -->
<string name="course_follow_end">종료</string>
<!-- en -->
<string name="course_follow_end">End</string>
<!-- ja -->
<string name="course_follow_end">終了</string>
```

### course_follow_end_confirm_desc

```xml
<!-- ko -->
<string name="course_follow_end_confirm_desc">지금 종료하면 진행 화면을 나가요. 나중에 코스에서 다시 이어갈 수 있어요.</string>
<!-- en -->
<string name="course_follow_end_confirm_desc">Ending now leaves the tracking screen. You can resume from the course later.</string>
<!-- ja -->
<string name="course_follow_end_confirm_desc">今終了すると進行画面を離れます。あとでコースから続けられます。</string>
```

### course_follow_end_confirm_title

```xml
<!-- ko -->
<string name="course_follow_end_confirm_title">따라가기를 종료할까요?</string>
<!-- en -->
<string name="course_follow_end_confirm_title">End the course?</string>
<!-- ja -->
<string name="course_follow_end_confirm_title">フォローを終了しますか？</string>
```

### course_follow_guide_collapse

```xml
<!-- ko -->
<string name="course_follow_guide_collapse">가는 길 설명 접기</string>
<!-- en -->
<string name="course_follow_guide_collapse">Hide directions</string>
<!-- ja -->
<string name="course_follow_guide_collapse">ルート案内を閉じる</string>
```

### course_follow_guide_expand

```xml
<!-- ko -->
<string name="course_follow_guide_expand">가는 길 설명</string>
<!-- en -->
<string name="course_follow_guide_expand">Directions</string>
<!-- ja -->
<string name="course_follow_guide_expand">ルート案内</string>
```

### course_follow_leave_review

```xml
<!-- ko -->
<string name="course_follow_leave_review">리뷰 남기기</string>
<!-- en -->
<string name="course_follow_leave_review">Write a review</string>
<!-- ja -->
<string name="course_follow_leave_review">レビューを書く</string>
```

### course_follow_load_failed

```xml
<!-- ko -->
<string name="course_follow_load_failed">코스를 불러오지 못했어요.</string>
<!-- en -->
<string name="course_follow_load_failed">Couldn\'t load the course.</string>
<!-- ja -->
<string name="course_follow_load_failed">コースを読み込めませんでした。</string>
```

### course_follow_next_spot

```xml
<!-- ko -->
<string name="course_follow_next_spot">다음 스팟</string>
<!-- en -->
<string name="course_follow_next_spot">Next spot</string>
<!-- ja -->
<string name="course_follow_next_spot">次のスポット</string>
```

### course_follow_recording

```xml
<!-- ko -->
<string name="course_follow_recording">방문 기록 저장 중…</string>
<!-- en -->
<string name="course_follow_recording">Saving visit…</string>
<!-- ja -->
<string name="course_follow_recording">訪問を記録中…</string>
```

### course_follow_resume

```xml
<!-- ko -->
<string name="course_follow_resume">이어가기</string>
<!-- en -->
<string name="course_follow_resume">Resume</string>
<!-- ja -->
<string name="course_follow_resume">続きから</string>
```

### course_follow_retry

```xml
<!-- ko -->
<string name="course_follow_retry">다시 시도</string>
<!-- en -->
<string name="course_follow_retry">Retry</string>
<!-- ja -->
<string name="course_follow_retry">再試行</string>
```

### course_follow_route_count

```xml
<!-- ko -->
<string name="course_follow_route_count">코스 동선 %1$d</string>
<!-- en -->
<string name="course_follow_route_count">Course route %1$d</string>
<!-- ja -->
<string name="course_follow_route_count">コースの動線 %1$d</string>
```

### course_follow_route_detail

```xml
<!-- ko -->
<string name="course_follow_route_detail">상세 도보 경로</string>
<!-- en -->
<string name="course_follow_route_detail">Walking route</string>
<!-- ja -->
<string name="course_follow_route_detail">徒歩ルート詳細</string>
```

### course_follow_route_loading

```xml
<!-- ko -->
<string name="course_follow_route_loading">도보 경로 불러오는 중…</string>
<!-- en -->
<string name="course_follow_route_loading">Loading walking route…</string>
<!-- ja -->
<string name="course_follow_route_loading">徒歩ルートを読み込み中…</string>
```

### course_follow_route_start

```xml
<!-- ko -->
<string name="course_follow_route_start">여기서 코스를 시작해요</string>
<!-- en -->
<string name="course_follow_route_start">Your course starts here</string>
<!-- ja -->
<string name="course_follow_route_start">ここからコースを始めます</string>
```

### course_follow_route_to_dest

```xml
<!-- ko -->
<string name="course_follow_route_to_dest">%1$s 까지</string>
<!-- en -->
<string name="course_follow_route_to_dest">To %1$s</string>
<!-- ja -->
<string name="course_follow_route_to_dest">%1$s まで</string>
```

### course_follow_route_walk_minutes

```xml
<!-- ko -->
<string name="course_follow_route_walk_minutes">도보 %1$d분</string>
<!-- en -->
<string name="course_follow_route_walk_minutes">%1$d min walk</string>
<!-- ja -->
<string name="course_follow_route_walk_minutes">徒歩%1$d分</string>
```

### course_follow_skip

```xml
<!-- ko -->
<string name="course_follow_skip">건너뛰기</string>
<!-- en -->
<string name="course_follow_skip">Skip</string>
<!-- ja -->
<string name="course_follow_skip">スキップ</string>
```

### course_follow_start

```xml
<!-- ko -->
<string name="course_follow_start">이 코스 따라가기</string>
<!-- en -->
<string name="course_follow_start">Follow this course</string>
<!-- ja -->
<string name="course_follow_start">このコースをフォロー</string>
```

### course_follow_start_here

```xml
<!-- ko -->
<string name="course_follow_start_here">여기서 출발</string>
<!-- en -->
<string name="course_follow_start_here">Start here</string>
<!-- ja -->
<string name="course_follow_start_here">ここから出発</string>
```

### course_follow_stat_distance

```xml
<!-- ko -->
<string name="course_follow_stat_distance">이동 거리</string>
<!-- en -->
<string name="course_follow_stat_distance">Distance</string>
<!-- ja -->
<string name="course_follow_stat_distance">移動距離</string>
```

### course_follow_stat_elapsed

```xml
<!-- ko -->
<string name="course_follow_stat_elapsed">소요 시간</string>
<!-- en -->
<string name="course_follow_stat_elapsed">Time taken</string>
<!-- ja -->
<string name="course_follow_stat_elapsed">所要時間</string>
```

### course_follow_stat_visited

```xml
<!-- ko -->
<string name="course_follow_stat_visited">방문 스팟</string>
<!-- en -->
<string name="course_follow_stat_visited">Spots visited</string>
<!-- ja -->
<string name="course_follow_stat_visited">訪問スポット</string>
```

### course_follow_tracking_title

```xml
<!-- ko -->
<string name="course_follow_tracking_title">%1$s · %2$d/%3$d 진행 중</string>
<!-- en -->
<string name="course_follow_tracking_title">%1$s · %2$d/%3$d in progress</string>
<!-- ja -->
<string name="course_follow_tracking_title">%1$s · %2$d/%3$d 進行中</string>
```

### course_follow_visit_done

```xml
<!-- ko -->
<string name="course_follow_visit_done">방문 완료</string>
<!-- en -->
<string name="course_follow_visit_done">Visited</string>
<!-- ja -->
<string name="course_follow_visit_done">訪問完了</string>
```

### course_follow_visited

```xml
<!-- ko -->
<string name="course_follow_visited">방문함</string>
<!-- en -->
<string name="course_follow_visited">Visited</string>
<!-- ja -->
<string name="course_follow_visited">訪問済み</string>
```

### course_follow_visited_course

```xml
<!-- ko -->
<string name="course_follow_visited_course">다녀온 코스</string>
<!-- en -->
<string name="course_follow_visited_course">Your route</string>
<!-- ja -->
<string name="course_follow_visited_course">歩いたコース</string>
```

### course_image_size_limit

```xml
<!-- ko -->
<string name="course_image_size_limit">5MB 이하 이미지만 추가할 수 있어요.</string>
<!-- en -->
<string name="course_image_size_limit">Only images up to 5 MB can be added.</string>
<!-- ja -->
<string name="course_image_size_limit">5MB以下の画像のみ追加できます。</string>
```

### course_info_description_label

```xml
<!-- ko -->
<string name="course_info_description_label">코스 설명</string>
<!-- en -->
<string name="course_info_description_label">Course description</string>
<!-- ja -->
<string name="course_info_description_label">コースの説明</string>
```

### course_info_description_placeholder

```xml
<!-- ko -->
<string name="course_info_description_placeholder">코스 설명을 입력하세요</string>
<!-- en -->
<string name="course_info_description_placeholder">Enter a course description</string>
<!-- ja -->
<string name="course_info_description_placeholder">コースの説明を入力してください</string>
```

### course_info_name_label

```xml
<!-- ko -->
<string name="course_info_name_label">코스 이름</string>
<!-- en -->
<string name="course_info_name_label">Course name</string>
<!-- ja -->
<string name="course_info_name_label">コース名</string>
```

### course_info_name_placeholder

```xml
<!-- ko -->
<string name="course_info_name_placeholder">코스 이름을 입력하세요</string>
<!-- en -->
<string name="course_info_name_placeholder">Enter a course name</string>
<!-- ja -->
<string name="course_info_name_placeholder">コース名を入力してください</string>
```

### course_info_thumbnail_label

```xml
<!-- ko -->
<string name="course_info_thumbnail_label">썸네일 이미지</string>
<!-- en -->
<string name="course_info_thumbnail_label">Thumbnail image</string>
<!-- ja -->
<string name="course_info_thumbnail_label">サムネイル画像</string>
```

### course_loading_content

```xml
<!-- ko -->
<string name="course_loading_content">콘텐츠를 불러오는 중</string>
<!-- en -->
<string name="course_loading_content">Loading content</string>
<!-- ja -->
<string name="course_loading_content">コンテンツを読み込み中</string>
```

### course_loading_create

```xml
<!-- ko -->
<string name="course_loading_create">코스 작성 화면을 불러오는 중</string>
<!-- en -->
<string name="course_loading_create">Loading course editor</string>
<!-- ja -->
<string name="course_loading_create">コース作成画面を読み込み中</string>
```

### course_loading_detail

```xml
<!-- ko -->
<string name="course_loading_detail">코스를 불러오는 중</string>
<!-- en -->
<string name="course_loading_detail">Loading course</string>
<!-- ja -->
<string name="course_loading_detail">コースを読み込み中</string>
```

### course_loading_drafts

```xml
<!-- ko -->
<string name="course_loading_drafts">임시저장한 코스를 불러오는 중</string>
<!-- en -->
<string name="course_loading_drafts">Loading saved drafts</string>
<!-- ja -->
<string name="course_loading_drafts">下書きのコースを読み込み中</string>
```

### course_loading_follow

```xml
<!-- ko -->
<string name="course_loading_follow">따라갈 코스를 불러오는 중</string>
<!-- en -->
<string name="course_loading_follow">Loading course to follow</string>
<!-- ja -->
<string name="course_loading_follow">フォローするコースを読み込み中</string>
```

### course_loading_list

```xml
<!-- ko -->
<string name="course_loading_list">목록을 불러오는 중</string>
<!-- en -->
<string name="course_loading_list">Loading list</string>
<!-- ja -->
<string name="course_loading_list">リストを読み込み中</string>
```

### course_loading_reviews

```xml
<!-- ko -->
<string name="course_loading_reviews">후기를 불러오는 중</string>
<!-- en -->
<string name="course_loading_reviews">Loading reviews</string>
<!-- ja -->
<string name="course_loading_reviews">レビューを読み込み中</string>
```

### course_place_card_note_placeholder

```xml
<!-- ko -->
<string name="course_place_card_note_placeholder">이곳에 한마디 남기기…</string>
<!-- en -->
<string name="course_place_card_note_placeholder">Add a quick note…</string>
<!-- ja -->
<string name="course_place_card_note_placeholder">ひとことメモを残す…</string>
```

### course_place_review_empty_subtitle

```xml
<!-- ko -->
<string name="course_place_review_empty_subtitle">다녀오셨다면 첫 후기를 남겨주세요.</string>
<!-- en -->
<string name="course_place_review_empty_subtitle">Been here? Be the first to leave a review.</string>
<!-- ja -->
<string name="course_place_review_empty_subtitle">訪れたことがあれば、最初の口コミを残してください。</string>
```

### course_place_review_empty_title

```xml
<!-- ko -->
<string name="course_place_review_empty_title">아직 등록된 후기가 없어요</string>
<!-- en -->
<string name="course_place_review_empty_title">No reviews yet</string>
<!-- ja -->
<string name="course_place_review_empty_title">まだ口コミがありません</string>
```

### course_place_review_write

```xml
<!-- ko -->
<string name="course_place_review_write">후기 남기기</string>
<!-- en -->
<string name="course_place_review_write">Write a review</string>
<!-- ja -->
<string name="course_place_review_write">口コミを書く</string>
```

### course_place_review_write_first

```xml
<!-- ko -->
<string name="course_place_review_write_first">첫 후기 남기기</string>
<!-- en -->
<string name="course_place_review_write_first">Write first review</string>
<!-- ja -->
<string name="course_place_review_write_first">最初の口コミを書く</string>
```

### course_place_search_add_new_place

```xml
<!-- ko -->
<string name="course_place_search_add_new_place">새로운 위치 추가</string>
<!-- en -->
<string name="course_place_search_add_new_place">Add a new place</string>
<!-- ja -->
<string name="course_place_search_add_new_place">新しい場所を追加</string>
```

### course_place_search_cancel

```xml
<!-- ko -->
<string name="course_place_search_cancel">취소</string>
<!-- en -->
<string name="course_place_search_cancel">Cancel</string>
<!-- ja -->
<string name="course_place_search_cancel">キャンセル</string>
```

### course_place_search_clear_query

```xml
<!-- ko -->
<string name="course_place_search_clear_query">검색어 지우기</string>
<!-- en -->
<string name="course_place_search_clear_query">Clear search</string>
<!-- ja -->
<string name="course_place_search_clear_query">検索語を消去</string>
```

### course_place_search_confirm

```xml
<!-- ko -->
<string name="course_place_search_confirm">담기 완료</string>
<!-- en -->
<string name="course_place_search_confirm">Add places</string>
<!-- ja -->
<string name="course_place_search_confirm">追加する</string>
```

### course_place_search_empty_prompt

```xml
<!-- ko -->
<string name="course_place_search_empty_prompt">장소나 주소를 검색해 보세요</string>
<!-- en -->
<string name="course_place_search_empty_prompt">Search for a place or address</string>
<!-- ja -->
<string name="course_place_search_empty_prompt">場所や住所を検索してみましょう</string>
```

### course_place_search_empty_result

```xml
<!-- ko -->
<string name="course_place_search_empty_result">검색 결과가 없어요</string>
<!-- en -->
<string name="course_place_search_empty_result">No results found</string>
<!-- ja -->
<string name="course_place_search_empty_result">検索結果がありません</string>
```

### course_place_search_input_hint

```xml
<!-- ko -->
<string name="course_place_search_input_hint">장소 · 주소 검색</string>
<!-- en -->
<string name="course_place_search_input_hint">Search place or address</string>
<!-- ja -->
<string name="course_place_search_input_hint">場所・住所を検索</string>
```

### course_place_search_limit_alert

```xml
<!-- ko -->
<string name="course_place_search_limit_alert">장소는 최대 %1$d개만 담을 수 있습니다.</string>
<!-- en -->
<string name="course_place_search_limit_alert">You can add up to %1$d places.</string>
<!-- ja -->
<string name="course_place_search_limit_alert">場所は最大%1$dか所まで追加できます。</string>
```

### course_place_search_map_result_count

```xml
<!-- ko -->
<string name="course_place_search_map_result_count">지도 검색 결과 %1$d</string>
<!-- en -->
<string name="course_place_search_map_result_count">Map results %1$d</string>
<!-- ja -->
<string name="course_place_search_map_result_count">地図の検索結果 %1$d</string>
```

### course_place_search_result_count

```xml
<!-- ko -->
<string name="course_place_search_result_count">검색 결과 %1$d</string>
<!-- en -->
<string name="course_place_search_result_count">Results %1$d</string>
<!-- ja -->
<string name="course_place_search_result_count">検索結果 %1$d</string>
```

### course_place_search_searching

```xml
<!-- ko -->
<string name="course_place_search_searching">장소를 찾고 있어요</string>
<!-- en -->
<string name="course_place_search_searching">Finding places…</string>
<!-- ja -->
<string name="course_place_search_searching">場所を探しています…</string>
```

### course_place_search_selected_count

```xml
<!-- ko -->
<string name="course_place_search_selected_count">%1$d곳 선택됨</string>
<!-- en -->
<string name="course_place_search_selected_count">%1$d selected</string>
<!-- ja -->
<string name="course_place_search_selected_count">%1$dか所を選択中</string>
```

### course_place_selected_order

```xml
<!-- ko -->
<string name="course_place_selected_order">선택됨, %1$d번째</string>
<!-- en -->
<string name="course_place_selected_order">Selected, number %1$d</string>
<!-- ja -->
<string name="course_place_selected_order">選択済み、%1$d番目</string>
```

### course_place_sheet_close_detail

```xml
<!-- ko -->
<string name="course_place_sheet_close_detail">장소 상세 닫기</string>
<!-- en -->
<string name="course_place_sheet_close_detail">Close place details</string>
<!-- ja -->
<string name="course_place_sheet_close_detail">場所の詳細を閉じる</string>
```

### course_place_sheet_close_map

```xml
<!-- ko -->
<string name="course_place_sheet_close_map">지도 닫기</string>
<!-- en -->
<string name="course_place_sheet_close_map">Close map</string>
<!-- ja -->
<string name="course_place_sheet_close_map">地図を閉じる</string>
```

### course_place_sheet_directions

```xml
<!-- ko -->
<string name="course_place_sheet_directions">길찾기</string>
<!-- en -->
<string name="course_place_sheet_directions">Directions</string>
<!-- ja -->
<string name="course_place_sheet_directions">ルート案内</string>
```

### course_place_sheet_no_location

```xml
<!-- ko -->
<string name="course_place_sheet_no_location">위치 정보가 없어요</string>
<!-- en -->
<string name="course_place_sheet_no_location">No location info</string>
<!-- ja -->
<string name="course_place_sheet_no_location">位置情報がありません</string>
```

### course_place_sheet_opening_hours

```xml
<!-- ko -->
<string name="course_place_sheet_opening_hours">운영시간</string>
<!-- en -->
<string name="course_place_sheet_opening_hours">Opening hours</string>
<!-- ja -->
<string name="course_place_sheet_opening_hours">営業時間</string>
```

### course_place_sheet_opening_hours_collapse

```xml
<!-- ko -->
<string name="course_place_sheet_opening_hours_collapse">운영시간 접기</string>
<!-- en -->
<string name="course_place_sheet_opening_hours_collapse">Collapse hours</string>
<!-- ja -->
<string name="course_place_sheet_opening_hours_collapse">営業時間を閉じる</string>
```

### course_place_sheet_opening_hours_expand

```xml
<!-- ko -->
<string name="course_place_sheet_opening_hours_expand">요일별 운영시간 펼치기</string>
<!-- en -->
<string name="course_place_sheet_opening_hours_expand">Show hours by day</string>
<!-- ja -->
<string name="course_place_sheet_opening_hours_expand">曜日別の営業時間を表示</string>
```

### course_place_sheet_overview_back

```xml
<!-- ko -->
<string name="course_place_sheet_overview_back">현재 장소로 돌아가기</string>
<!-- en -->
<string name="course_place_sheet_overview_back">Back to current place</string>
<!-- ja -->
<string name="course_place_sheet_overview_back">現在の場所に戻る</string>
```

### course_place_sheet_overview_open

```xml
<!-- ko -->
<string name="course_place_sheet_overview_open">코스 전체 보기</string>
<!-- en -->
<string name="course_place_sheet_overview_open">View whole course</string>
<!-- ja -->
<string name="course_place_sheet_overview_open">コース全体を見る</string>
```

### course_place_sheet_place_count

```xml
<!-- ko -->
<string name="course_place_sheet_place_count">%1$d곳</string>
<!-- en -->
<string name="course_place_sheet_place_count">%1$d places</string>
<!-- ja -->
<string name="course_place_sheet_place_count">%1$dか所</string>
```

### course_place_sheet_review_count

```xml
<!-- ko -->
<string name="course_place_sheet_review_count">리뷰 %1$s</string>
<!-- en -->
<string name="course_place_sheet_review_count">Reviews %1$s</string>
<!-- ja -->
<string name="course_place_sheet_review_count">レビュー %1$s</string>
```

### course_place_sheet_review_delete_desc

```xml
<!-- ko -->
<string name="course_place_sheet_review_delete_desc">삭제한 후기는 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_place_sheet_review_delete_desc">Deleted reviews can\'t be restored.</string>
<!-- ja -->
<string name="course_place_sheet_review_delete_desc">削除したレビューは元に戻せません。</string>
```

### course_place_sheet_review_delete_title

```xml
<!-- ko -->
<string name="course_place_sheet_review_delete_title">후기를 삭제할까요?</string>
<!-- en -->
<string name="course_place_sheet_review_delete_title">Delete this review?</string>
<!-- ja -->
<string name="course_place_sheet_review_delete_title">レビューを削除しますか?</string>
```

### course_place_sheet_review_load_failed

```xml
<!-- ko -->
<string name="course_place_sheet_review_load_failed">후기 정보를 불러오지 못했어요</string>
<!-- en -->
<string name="course_place_sheet_review_load_failed">Couldn\'t load reviews</string>
<!-- ja -->
<string name="course_place_sheet_review_load_failed">レビューを読み込めませんでした</string>
```

### course_place_sheet_review_title

```xml
<!-- ko -->
<string name="course_place_sheet_review_title">후기</string>
<!-- en -->
<string name="course_place_sheet_review_title">Reviews</string>
<!-- ja -->
<string name="course_place_sheet_review_title">レビュー</string>
```

### course_place_sheet_save

```xml
<!-- ko -->
<string name="course_place_sheet_save">저장</string>
<!-- en -->
<string name="course_place_sheet_save">Save</string>
<!-- ja -->
<string name="course_place_sheet_save">保存</string>
```

### course_place_sheet_saved

```xml
<!-- ko -->
<string name="course_place_sheet_saved">저장됨</string>
<!-- en -->
<string name="course_place_sheet_saved">Saved</string>
<!-- ja -->
<string name="course_place_sheet_saved">保存済み</string>
```

### course_place_sheet_step_next

```xml
<!-- ko -->
<string name="course_place_sheet_step_next">다음</string>
<!-- en -->
<string name="course_place_sheet_step_next">Next</string>
<!-- ja -->
<string name="course_place_sheet_step_next">次へ</string>
```

### course_place_sheet_step_previous

```xml
<!-- ko -->
<string name="course_place_sheet_step_previous">이전</string>
<!-- en -->
<string name="course_place_sheet_step_previous">Previous</string>
<!-- ja -->
<string name="course_place_sheet_step_previous">前へ</string>
```

### course_place_sheet_tip_author

```xml
<!-- ko -->
<string name="course_place_sheet_tip_author">Tip.%1$s</string>
<!-- en -->
<string name="course_place_sheet_tip_author">Tip.%1$s</string>
<!-- ja -->
<string name="course_place_sheet_tip_author">ヒント：%1$s</string>
```

### course_plan_date_clear

```xml
<!-- ko -->
<string name="course_plan_date_clear">날짜 미정으로 변경</string>
<!-- en -->
<string name="course_plan_date_clear">Clear date</string>
<!-- ja -->
<string name="course_plan_date_clear">日付を未定にする</string>
```

### course_plan_date_label

```xml
<!-- ko -->
<string name="course_plan_date_label">가려는 날 (선택)</string>
<!-- en -->
<string name="course_plan_date_label">Planned date (optional)</string>
<!-- ja -->
<string name="course_plan_date_label">訪問予定日（任意）</string>
```

### course_plan_date_unset

```xml
<!-- ko -->
<string name="course_plan_date_unset">미정 · 날짜 선택</string>
<!-- en -->
<string name="course_plan_date_unset">Not set · Pick a date</string>
<!-- ja -->
<string name="course_plan_date_unset">未定・日付を選択</string>
```

### course_plan_detail_back

```xml
<!-- ko -->
<string name="course_plan_detail_back">뒤로</string>
<!-- en -->
<string name="course_plan_detail_back">Back</string>
<!-- ja -->
<string name="course_plan_detail_back">戻る</string>
```

### course_plan_detail_delete_cancel

```xml
<!-- ko -->
<string name="course_plan_detail_delete_cancel">취소</string>
<!-- en -->
<string name="course_plan_detail_delete_cancel">Cancel</string>
<!-- ja -->
<string name="course_plan_detail_delete_cancel">キャンセル</string>
```

### course_plan_detail_delete_confirm

```xml
<!-- ko -->
<string name="course_plan_detail_delete_confirm">삭제</string>
<!-- en -->
<string name="course_plan_detail_delete_confirm">Delete</string>
<!-- ja -->
<string name="course_plan_detail_delete_confirm">削除</string>
```

### course_plan_detail_delete_description

```xml
<!-- ko -->
<string name="course_plan_detail_delete_description">삭제한 계획은 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_plan_detail_delete_description">Deleted plans can\'t be restored.</string>
<!-- ja -->
<string name="course_plan_detail_delete_description">削除したプランは元に戻せません。</string>
```

### course_plan_detail_delete_title

```xml
<!-- ko -->
<string name="course_plan_detail_delete_title">계획을 삭제할까요?</string>
<!-- en -->
<string name="course_plan_detail_delete_title">Delete this plan?</string>
<!-- ja -->
<string name="course_plan_detail_delete_title">プランを削除しますか？</string>
```

### course_plan_detail_edit_cancel

```xml
<!-- ko -->
<string name="course_plan_detail_edit_cancel">취소</string>
<!-- en -->
<string name="course_plan_detail_edit_cancel">Cancel</string>
<!-- ja -->
<string name="course_plan_detail_edit_cancel">キャンセル</string>
```

### course_plan_detail_edit_date_label

```xml
<!-- ko -->
<string name="course_plan_detail_edit_date_label">방문 날짜 (선택)</string>
<!-- en -->
<string name="course_plan_detail_edit_date_label">Visit date (optional)</string>
<!-- ja -->
<string name="course_plan_detail_edit_date_label">訪問日（任意）</string>
```

### course_plan_detail_edit_description

```xml
<!-- ko -->
<string name="course_plan_detail_edit_description">장소 구성은 그대로 두고 제목·메모·방문 날짜를 바꿔요.</string>
<!-- en -->
<string name="course_plan_detail_edit_description">Keep the places and change the title, memo, or date.</string>
<!-- ja -->
<string name="course_plan_detail_edit_description">スポットの構成はそのままに、タイトル・メモ・訪問日を変更します。</string>
```

### course_plan_detail_edit_memo_placeholder

```xml
<!-- ko -->
<string name="course_plan_detail_edit_memo_placeholder">메모 (선택)</string>
<!-- en -->
<string name="course_plan_detail_edit_memo_placeholder">Memo (optional)</string>
<!-- ja -->
<string name="course_plan_detail_edit_memo_placeholder">メモ（任意）</string>
```

### course_plan_detail_edit_save

```xml
<!-- ko -->
<string name="course_plan_detail_edit_save">저장</string>
<!-- en -->
<string name="course_plan_detail_edit_save">Save</string>
<!-- ja -->
<string name="course_plan_detail_edit_save">保存</string>
```

### course_plan_detail_edit_title

```xml
<!-- ko -->
<string name="course_plan_detail_edit_title">계획 수정</string>
<!-- en -->
<string name="course_plan_detail_edit_title">Edit plan</string>
<!-- ja -->
<string name="course_plan_detail_edit_title">プランを編集</string>
```

### course_plan_detail_edit_title_placeholder

```xml
<!-- ko -->
<string name="course_plan_detail_edit_title_placeholder">계획 이름</string>
<!-- en -->
<string name="course_plan_detail_edit_title_placeholder">Plan name</string>
<!-- ja -->
<string name="course_plan_detail_edit_title_placeholder">プラン名</string>
```

### course_plan_detail_map_title

```xml
<!-- ko -->
<string name="course_plan_detail_map_title">계획 한눈에 보기</string>
<!-- en -->
<string name="course_plan_detail_map_title">Plan at a glance</string>
<!-- ja -->
<string name="course_plan_detail_map_title">プランをひと目で</string>
```

### course_plan_detail_memo_title

```xml
<!-- ko -->
<string name="course_plan_detail_memo_title">계획 메모</string>
<!-- en -->
<string name="course_plan_detail_memo_title">Plan memo</string>
<!-- ja -->
<string name="course_plan_detail_memo_title">プランのメモ</string>
```

### course_plan_detail_menu_delete

```xml
<!-- ko -->
<string name="course_plan_detail_menu_delete">계획 삭제</string>
<!-- en -->
<string name="course_plan_detail_menu_delete">Delete plan</string>
<!-- ja -->
<string name="course_plan_detail_menu_delete">プランを削除</string>
```

### course_plan_detail_menu_edit

```xml
<!-- ko -->
<string name="course_plan_detail_menu_edit">계획 수정</string>
<!-- en -->
<string name="course_plan_detail_menu_edit">Edit plan</string>
<!-- ja -->
<string name="course_plan_detail_menu_edit">プランを編集</string>
```

### course_plan_detail_menu_processing

```xml
<!-- ko -->
<string name="course_plan_detail_menu_processing">처리 중…</string>
<!-- en -->
<string name="course_plan_detail_menu_processing">Processing…</string>
<!-- ja -->
<string name="course_plan_detail_menu_processing">処理中…</string>
```

### course_plan_detail_place_count

```xml
<!-- ko -->
<string name="course_plan_detail_place_count">%1$d곳</string>
<!-- en -->
<string name="course_plan_detail_place_count">%1$d places</string>
<!-- ja -->
<string name="course_plan_detail_place_count">%1$dか所</string>
```

### course_plan_detail_places_title

```xml
<!-- ko -->
<string name="course_plan_detail_places_title">계획 속 장소</string>
<!-- en -->
<string name="course_plan_detail_places_title">Places in plan</string>
<!-- ja -->
<string name="course_plan_detail_places_title">プラン内のスポット</string>
```

### course_plan_detail_retry

```xml
<!-- ko -->
<string name="course_plan_detail_retry">다시 시도</string>
<!-- en -->
<string name="course_plan_detail_retry">Retry</string>
<!-- ja -->
<string name="course_plan_detail_retry">再試行</string>
```

### course_plan_detail_settings

```xml
<!-- ko -->
<string name="course_plan_detail_settings">계획 설정</string>
<!-- en -->
<string name="course_plan_detail_settings">Plan settings</string>
<!-- ja -->
<string name="course_plan_detail_settings">プラン設定</string>
```

### course_plan_detail_source_label

```xml
<!-- ko -->
<string name="course_plan_detail_source_label">코스에서 복제한 계획</string>
<!-- en -->
<string name="course_plan_detail_source_label">Copied from a course</string>
<!-- ja -->
<string name="course_plan_detail_source_label">コースから複製したプラン</string>
```

### course_plan_detail_source_view_origin

```xml
<!-- ko -->
<string name="course_plan_detail_source_view_origin">원본 코스 보기</string>
<!-- en -->
<string name="course_plan_detail_source_view_origin">View original course</string>
<!-- ja -->
<string name="course_plan_detail_source_view_origin">元のコースを見る</string>
```

### course_plan_detail_title

```xml
<!-- ko -->
<string name="course_plan_detail_title">계획 상세</string>
<!-- en -->
<string name="course_plan_detail_title">Plan details</string>
<!-- ja -->
<string name="course_plan_detail_title">プランの詳細</string>
```

### course_plan_memo_label

```xml
<!-- ko -->
<string name="course_plan_memo_label">전체 메모 (선택 · %1$d/500)</string>
<!-- en -->
<string name="course_plan_memo_label">Memo (optional · %1$d/500)</string>
<!-- ja -->
<string name="course_plan_memo_label">メモ（任意・%1$d/500）</string>
```

### course_plan_memo_placeholder

```xml
<!-- ko -->
<string name="course_plan_memo_placeholder">기억할 내용을 남겨 보세요</string>
<!-- en -->
<string name="course_plan_memo_placeholder">Jot down anything to remember</string>
<!-- ja -->
<string name="course_plan_memo_placeholder">覚えておきたいことを残しましょう</string>
```

### course_plan_title_label

```xml
<!-- ko -->
<string name="course_plan_title_label">계획 제목 (선택 · %1$d/200)</string>
<!-- en -->
<string name="course_plan_title_label">Plan title (optional · %1$d/200)</string>
<!-- ja -->
<string name="course_plan_title_label">プランのタイトル（任意・%1$d/200）</string>
```

### course_plan_title_placeholder

```xml
<!-- ko -->
<string name="course_plan_title_placeholder">계획 제목을 입력하세요</string>
<!-- en -->
<string name="course_plan_title_placeholder">Enter a plan title</string>
<!-- ja -->
<string name="course_plan_title_placeholder">プランのタイトルを入力してください</string>
```

### course_recommended_tags

```xml
<!-- ko -->
<string name="course_recommended_tags">추천 태그</string>
<!-- en -->
<string name="course_recommended_tags">Suggested tags</string>
<!-- ja -->
<string name="course_recommended_tags">おすすめタグ</string>
```

### course_remove_place_confirm_course

```xml
<!-- ko -->
<string name="course_remove_place_confirm_course">코스에서 빼기</string>
<!-- en -->
<string name="course_remove_place_confirm_course">Remove from course</string>
<!-- ja -->
<string name="course_remove_place_confirm_course">コースから外す</string>
```

### course_remove_place_confirm_plan

```xml
<!-- ko -->
<string name="course_remove_place_confirm_plan">계획에서 빼기</string>
<!-- en -->
<string name="course_remove_place_confirm_plan">Remove from plan</string>
<!-- ja -->
<string name="course_remove_place_confirm_plan">プランから外す</string>
```

### course_remove_place_question_course

```xml
<!-- ko -->
<string name="course_remove_place_question_course">이 장소를 코스에서 뺄까요?</string>
<!-- en -->
<string name="course_remove_place_question_course">Remove this place from the course?</string>
<!-- ja -->
<string name="course_remove_place_question_course">この場所をコースから外しますか？</string>
```

### course_remove_place_question_plan

```xml
<!-- ko -->
<string name="course_remove_place_question_plan">이 장소를 계획에서 뺄까요?</string>
<!-- en -->
<string name="course_remove_place_question_plan">Remove this place from the plan?</string>
<!-- ja -->
<string name="course_remove_place_question_plan">この場所をプランから外しますか？</string>
```

### course_remove_tag

```xml
<!-- ko -->
<string name="course_remove_tag">%1$s 태그 삭제</string>
<!-- en -->
<string name="course_remove_tag">Remove tag %1$s</string>
<!-- ja -->
<string name="course_remove_tag">タグ「%1$s」を削除</string>
```

### course_review_delete

```xml
<!-- ko -->
<string name="course_review_delete">내 후기 삭제</string>
<!-- en -->
<string name="course_review_delete">Delete my review</string>
<!-- ja -->
<string name="course_review_delete">自分のレビューを削除</string>
```

### course_review_empty

```xml
<!-- ko -->
<string name="course_review_empty">아직 후기가 없어요. 코스를 다녀온 경험을 나눠 주세요.</string>
<!-- en -->
<string name="course_review_empty">No reviews yet. Share your experience on this course.</string>
<!-- ja -->
<string name="course_review_empty">まだレビューがありません。このコースの体験をシェアしてください。</string>
```

### course_review_header_title

```xml
<!-- ko -->
<string name="course_review_header_title">후기</string>
<!-- en -->
<string name="course_review_header_title">Reviews</string>
<!-- ja -->
<string name="course_review_header_title">レビュー</string>
```

### course_review_list_back

```xml
<!-- ko -->
<string name="course_review_list_back">뒤로</string>
<!-- en -->
<string name="course_review_list_back">Back</string>
<!-- ja -->
<string name="course_review_list_back">戻る</string>
```

### course_review_list_delete_confirm

```xml
<!-- ko -->
<string name="course_review_list_delete_confirm">삭제</string>
<!-- en -->
<string name="course_review_list_delete_confirm">Delete</string>
<!-- ja -->
<string name="course_review_list_delete_confirm">削除</string>
```

### course_review_list_delete_desc

```xml
<!-- ko -->
<string name="course_review_list_delete_desc">삭제한 후기는 되돌릴 수 없어요.</string>
<!-- en -->
<string name="course_review_list_delete_desc">Deleted reviews can\'t be restored.</string>
<!-- ja -->
<string name="course_review_list_delete_desc">削除したレビューは元に戻せません。</string>
```

### course_review_list_delete_dismiss

```xml
<!-- ko -->
<string name="course_review_list_delete_dismiss">취소</string>
<!-- en -->
<string name="course_review_list_delete_dismiss">Cancel</string>
<!-- ja -->
<string name="course_review_list_delete_dismiss">キャンセル</string>
```

### course_review_list_delete_title

```xml
<!-- ko -->
<string name="course_review_list_delete_title">후기를 삭제할까요?</string>
<!-- en -->
<string name="course_review_list_delete_title">Delete this review?</string>
<!-- ja -->
<string name="course_review_list_delete_title">レビューを削除しますか?</string>
```

### course_review_list_empty_desc

```xml
<!-- ko -->
<string name="course_review_list_empty_desc">코스를 다녀온 경험을 나눠 주세요.\n별점만 남겨도 다음 여행에 도움이 돼요.</string>
<!-- en -->
<string name="course_review_list_empty_desc">Share your experience on this course.\nEven a rating helps the next traveler.</string>
<!-- ja -->
<string name="course_review_list_empty_desc">このコースの体験をシェアしてください。\n星の評価だけでも次の旅の参考になります。</string>
```

### course_review_list_empty_title

```xml
<!-- ko -->
<string name="course_review_list_empty_title">첫 후기를 기다리고 있어요</string>
<!-- en -->
<string name="course_review_list_empty_title">Be the first to review</string>
<!-- ja -->
<string name="course_review_list_empty_title">最初のレビューを待っています</string>
```

### course_review_list_load_error

```xml
<!-- ko -->
<string name="course_review_list_load_error">후기를 불러오지 못했습니다.</string>
<!-- en -->
<string name="course_review_list_load_error">Couldn\'t load reviews.</string>
<!-- ja -->
<string name="course_review_list_load_error">レビューを読み込めませんでした。</string>
```

### course_review_list_photo_header

```xml
<!-- ko -->
<string name="course_review_list_photo_header">후기 사진</string>
<!-- en -->
<string name="course_review_list_photo_header">Review photos</string>
<!-- ja -->
<string name="course_review_list_photo_header">レビュー写真</string>
```

### course_review_list_photo_see_all

```xml
<!-- ko -->
<string name="course_review_list_photo_see_all">모두 보기</string>
<!-- en -->
<string name="course_review_list_photo_see_all">See all</string>
<!-- ja -->
<string name="course_review_list_photo_see_all">すべて見る</string>
```

### course_review_list_retry

```xml
<!-- ko -->
<string name="course_review_list_retry">다시 시도</string>
<!-- en -->
<string name="course_review_list_retry">Retry</string>
<!-- ja -->
<string name="course_review_list_retry">再試行</string>
```

### course_review_list_title

```xml
<!-- ko -->
<string name="course_review_list_title">코스 후기</string>
<!-- en -->
<string name="course_review_list_title">Course reviews</string>
<!-- ja -->
<string name="course_review_list_title">コースのレビュー</string>
```

### course_review_list_write

```xml
<!-- ko -->
<string name="course_review_list_write">후기 쓰기</string>
<!-- en -->
<string name="course_review_list_write">Write a review</string>
<!-- ja -->
<string name="course_review_list_write">レビューを書く</string>
```

### course_review_see_all

```xml
<!-- ko -->
<string name="course_review_see_all">전체보기</string>
<!-- en -->
<string name="course_review_see_all">See all</string>
<!-- ja -->
<string name="course_review_see_all">すべて見る</string>
```

### course_review_write_close

```xml
<!-- ko -->
<string name="course_review_write_close">후기 작성 닫기</string>
<!-- en -->
<string name="course_review_write_close">Close review</string>
<!-- ja -->
<string name="course_review_write_close">レビュー作成を閉じる</string>
```

### course_review_write_content_placeholder_course

```xml
<!-- ko -->
<string name="course_review_write_content_placeholder_course">코스를 따라간 경험을 적어 주세요. (선택)</string>
<!-- en -->
<string name="course_review_write_content_placeholder_course">Share your experience on this course. (Optional)</string>
<!-- ja -->
<string name="course_review_write_content_placeholder_course">このコースをたどった感想を書いてください。（任意）</string>
```

### course_review_write_content_placeholder_place

```xml
<!-- ko -->
<string name="course_review_write_content_placeholder_place">장소를 방문한 경험을 적어 주세요. (선택)</string>
<!-- en -->
<string name="course_review_write_content_placeholder_place">Share your experience at this place. (Optional)</string>
<!-- ja -->
<string name="course_review_write_content_placeholder_place">この場所を訪れた感想を書いてください。（任意）</string>
```

### course_review_write_content_section

```xml
<!-- ko -->
<string name="course_review_write_content_section">방문 경험을 들려주세요</string>
<!-- en -->
<string name="course_review_write_content_section">Tell us about your visit</string>
<!-- ja -->
<string name="course_review_write_content_section">訪問の感想を教えてください</string>
```

### course_review_write_keyword_remove

```xml
<!-- ko -->
<string name="course_review_write_keyword_remove">%1$s 선택 해제</string>
<!-- en -->
<string name="course_review_write_keyword_remove">Deselect %1$s</string>
<!-- ja -->
<string name="course_review_write_keyword_remove">%1$sの選択を解除</string>
```

### course_review_write_photo_section

```xml
<!-- ko -->
<string name="course_review_write_photo_section">사진을 더해 주세요</string>
<!-- en -->
<string name="course_review_write_photo_section">Add some photos</string>
<!-- ja -->
<string name="course_review_write_photo_section">写真を追加しましょう</string>
```

### course_review_write_rating_hint

```xml
<!-- ko -->
<string name="course_review_write_rating_hint">별점을 골라 주세요</string>
<!-- en -->
<string name="course_review_write_rating_hint">Pick a rating</string>
<!-- ja -->
<string name="course_review_write_rating_hint">評価を選んでください</string>
```

### course_review_write_rating_title_course

```xml
<!-- ko -->
<string name="course_review_write_rating_title_course">이 코스 어땠나요?</string>
<!-- en -->
<string name="course_review_write_rating_title_course">How was this course?</string>
<!-- ja -->
<string name="course_review_write_rating_title_course">このコースはいかがでしたか？</string>
```

### course_review_write_rating_title_place

```xml
<!-- ko -->
<string name="course_review_write_rating_title_place">이 장소 어땠나요?</string>
<!-- en -->
<string name="course_review_write_rating_title_place">How was this place?</string>
<!-- ja -->
<string name="course_review_write_rating_title_place">この場所はいかがでしたか？</string>
```

### course_review_write_selected_keywords_count

```xml
<!-- ko -->
<string name="course_review_write_selected_keywords_count">선택한 키워드 %1$d/%2$d</string>
<!-- en -->
<string name="course_review_write_selected_keywords_count">Selected keywords %1$d/%2$d</string>
<!-- ja -->
<string name="course_review_write_selected_keywords_count">選んだキーワード %1$d/%2$d</string>
```

### course_review_write_selected_keywords_empty

```xml
<!-- ko -->
<string name="course_review_write_selected_keywords_empty">선택한 키워드가 여기에 모여요</string>
<!-- en -->
<string name="course_review_write_selected_keywords_empty">Your selected keywords show up here</string>
<!-- ja -->
<string name="course_review_write_selected_keywords_empty">選んだキーワードがここに集まります</string>
```

### course_review_write_submit

```xml
<!-- ko -->
<string name="course_review_write_submit">후기 등록</string>
<!-- en -->
<string name="course_review_write_submit">Post review</string>
<!-- ja -->
<string name="course_review_write_submit">レビューを投稿</string>
```

### course_review_write_tag_guide

```xml
<!-- ko -->
<string name="course_review_write_tag_guide">좋았던 점을 최대 %1$d개 골라 주세요. (선택)</string>
<!-- en -->
<string name="course_review_write_tag_guide">Pick up to %1$d things you liked. (Optional)</string>
<!-- ja -->
<string name="course_review_write_tag_guide">良かった点を最大%1$d個選んでください。（任意）</string>
```

### course_review_write_tag_section

```xml
<!-- ko -->
<string name="course_review_write_tag_section">키워드로 표현해 주세요</string>
<!-- en -->
<string name="course_review_write_tag_section">Describe it with keywords</string>
<!-- ja -->
<string name="course_review_write_tag_section">キーワードで表現しましょう</string>
```

### course_review_write_title

```xml
<!-- ko -->
<string name="course_review_write_title">후기 쓰기</string>
<!-- en -->
<string name="course_review_write_title">Write a review</string>
<!-- ja -->
<string name="course_review_write_title">レビューを書く</string>
```

### course_route_map_my_location

```xml
<!-- ko -->
<string name="course_route_map_my_location">내 위치</string>
<!-- en -->
<string name="course_route_map_my_location">My location</string>
<!-- ja -->
<string name="course_route_map_my_location">現在地</string>
```

### course_saved_draft_toast

```xml
<!-- ko -->
<string name="course_saved_draft_toast">임시저장했어요</string>
<!-- en -->
<string name="course_saved_draft_toast">Draft saved</string>
<!-- ja -->
<string name="course_saved_draft_toast">下書きを保存しました</string>
```

### course_visibility_card_label

```xml
<!-- ko -->
<string name="course_visibility_card_label">공개 설정</string>
<!-- en -->
<string name="course_visibility_card_label">Visibility</string>
<!-- ja -->
<string name="course_visibility_card_label">公開設定</string>
```

### course_visibility_follower_helper

```xml
<!-- ko -->
<string name="course_visibility_follower_helper">팔로워에게만 공개돼요.</string>
<!-- en -->
<string name="course_visibility_follower_helper">Only your followers can see this.</string>
<!-- ja -->
<string name="course_visibility_follower_helper">フォロワーにのみ公開されます。</string>
```

### course_visibility_follower_label

```xml
<!-- ko -->
<string name="course_visibility_follower_label">팔로워</string>
<!-- en -->
<string name="course_visibility_follower_label">Followers</string>
<!-- ja -->
<string name="course_visibility_follower_label">フォロワー</string>
```

### course_visibility_private_helper

```xml
<!-- ko -->
<string name="course_visibility_private_helper">나만 볼 수 있어요.</string>
<!-- en -->
<string name="course_visibility_private_helper">Only you can see this.</string>
<!-- ja -->
<string name="course_visibility_private_helper">自分だけが見られます。</string>
```

### course_visibility_private_label

```xml
<!-- ko -->
<string name="course_visibility_private_label">비공개</string>
<!-- en -->
<string name="course_visibility_private_label">Private</string>
<!-- ja -->
<string name="course_visibility_private_label">非公開</string>
```

### course_visibility_public_helper

```xml
<!-- ko -->
<string name="course_visibility_public_helper">누구나 둘러보고 따라갈 수 있어요.</string>
<!-- en -->
<string name="course_visibility_public_helper">Anyone can browse and follow this course.</string>
<!-- ja -->
<string name="course_visibility_public_helper">誰でも閲覧してフォローできます。</string>
```

### course_visibility_public_label

```xml
<!-- ko -->
<string name="course_visibility_public_label">공개</string>
<!-- en -->
<string name="course_visibility_public_label">Public</string>
<!-- ja -->
<string name="course_visibility_public_label">公開</string>
```

## main/presentation/src/main/res/values/ai_chat_strings.xml

### ai_back

```xml
<!-- ko -->
<string name="ai_back">뒤로</string>
<!-- en -->
<string name="ai_back">Back</string>
<!-- ja -->
<string name="ai_back">戻る</string>
```

### ai_banner

```xml
<!-- ko -->
<string name="ai_banner">취향만 알려주면\n코스를 뚝딱 짜줘요</string>
<!-- en -->
<string name="ai_banner">Share your interests.\nFind your next course.</string>
<!-- ja -->
<string name="ai_banner">好みを教えるだけで\nおすすめコースをご提案</string>
```

### ai_beta

```xml
<!-- ko -->
<string name="ai_beta">베타</string>
<!-- en -->
<string name="ai_beta">Beta</string>
<!-- ja -->
<string name="ai_beta">ベータ</string>
```

### ai_login

```xml
<!-- ko -->
<string name="ai_login">로그인하기</string>
<!-- en -->
<string name="ai_login">Sign in</string>
<!-- ja -->
<string name="ai_login">ログイン</string>
```

### ai_login_prompt

```xml
<!-- ko -->
<string name="ai_login_prompt">로그인하고\n커미 AI에게 코스를 추천받아 보세요</string>
<!-- en -->
<string name="ai_login_prompt">Sign in to get\ncourse ideas from Courmy AI</string>
<!-- ja -->
<string name="ai_login_prompt">ログインして\nクルミ AIにコースをおすすめしてもらいましょう</string>
```

### ai_placeholder

```xml
<!-- ko -->
<string name="ai_placeholder">메시지를 입력하세요</string>
<!-- en -->
<string name="ai_placeholder">Enter a message</string>
<!-- ja -->
<string name="ai_placeholder">メッセージを入力</string>
```

### ai_recommend

```xml
<!-- ko -->
<string name="ai_recommend">코스 추천받기</string>
<!-- en -->
<string name="ai_recommend">Find a course</string>
<!-- ja -->
<string name="ai_recommend">コースを探す</string>
```

### ai_send

```xml
<!-- ko -->
<string name="ai_send">보내기</string>
<!-- en -->
<string name="ai_send">Send</string>
<!-- ja -->
<string name="ai_send">送信</string>
```

### ai_thinking

```xml
<!-- ko -->
<string name="ai_thinking">응답 준비 중</string>
<!-- en -->
<string name="ai_thinking">Preparing a response</string>
<!-- ja -->
<string name="ai_thinking">回答を準備中</string>
```

### ai_title

```xml
<!-- ko -->
<string name="ai_title">커미 AI</string>
<!-- en -->
<string name="ai_title">Courmy AI</string>
<!-- ja -->
<string name="ai_title">クルミ AI</string>
```

## main/presentation/src/main/res/values/district_names.xml

### district_11110

```xml
<!-- ko -->
<string name="district_11110">종로구</string>
<!-- en -->
<string name="district_11110">Jongno-gu</string>
<!-- ja -->
<string name="district_11110">チョンノ区</string>
```

### district_11140

```xml
<!-- ko -->
<string name="district_11140">중구</string>
<!-- en -->
<string name="district_11140">Jung-gu</string>
<!-- ja -->
<string name="district_11140">チュン区</string>
```

### district_11170

```xml
<!-- ko -->
<string name="district_11170">용산구</string>
<!-- en -->
<string name="district_11170">Yongsan-gu</string>
<!-- ja -->
<string name="district_11170">ヨンサン区</string>
```

### district_11200

```xml
<!-- ko -->
<string name="district_11200">성동구</string>
<!-- en -->
<string name="district_11200">Seongdong-gu</string>
<!-- ja -->
<string name="district_11200">ソンドン区</string>
```

### district_11215

```xml
<!-- ko -->
<string name="district_11215">광진구</string>
<!-- en -->
<string name="district_11215">Gwangjin-gu</string>
<!-- ja -->
<string name="district_11215">クァンジン区</string>
```

### district_11230

```xml
<!-- ko -->
<string name="district_11230">동대문구</string>
<!-- en -->
<string name="district_11230">Dongdaemun-gu</string>
<!-- ja -->
<string name="district_11230">トンデムン区</string>
```

### district_11260

```xml
<!-- ko -->
<string name="district_11260">중랑구</string>
<!-- en -->
<string name="district_11260">Jungnang-gu</string>
<!-- ja -->
<string name="district_11260">チュンナン区</string>
```

### district_11290

```xml
<!-- ko -->
<string name="district_11290">성북구</string>
<!-- en -->
<string name="district_11290">Seongbuk-gu</string>
<!-- ja -->
<string name="district_11290">ソンブク区</string>
```

### district_11305

```xml
<!-- ko -->
<string name="district_11305">강북구</string>
<!-- en -->
<string name="district_11305">Gangbuk-gu</string>
<!-- ja -->
<string name="district_11305">カンブク区</string>
```

### district_11320

```xml
<!-- ko -->
<string name="district_11320">도봉구</string>
<!-- en -->
<string name="district_11320">Dobong-gu</string>
<!-- ja -->
<string name="district_11320">トボン区</string>
```

### district_11350

```xml
<!-- ko -->
<string name="district_11350">노원구</string>
<!-- en -->
<string name="district_11350">Nowon-gu</string>
<!-- ja -->
<string name="district_11350">ノウォン区</string>
```

### district_11380

```xml
<!-- ko -->
<string name="district_11380">은평구</string>
<!-- en -->
<string name="district_11380">Eunpyeong-gu</string>
<!-- ja -->
<string name="district_11380">ウンピョン区</string>
```

### district_11410

```xml
<!-- ko -->
<string name="district_11410">서대문구</string>
<!-- en -->
<string name="district_11410">Seodaemun-gu</string>
<!-- ja -->
<string name="district_11410">ソデムン区</string>
```

### district_11440

```xml
<!-- ko -->
<string name="district_11440">마포구</string>
<!-- en -->
<string name="district_11440">Mapo-gu</string>
<!-- ja -->
<string name="district_11440">マポ区</string>
```

### district_11470

```xml
<!-- ko -->
<string name="district_11470">양천구</string>
<!-- en -->
<string name="district_11470">Yangcheon-gu</string>
<!-- ja -->
<string name="district_11470">ヤンチョン区</string>
```

### district_11500

```xml
<!-- ko -->
<string name="district_11500">강서구</string>
<!-- en -->
<string name="district_11500">Gangseo-gu</string>
<!-- ja -->
<string name="district_11500">カンソ区</string>
```

### district_11530

```xml
<!-- ko -->
<string name="district_11530">구로구</string>
<!-- en -->
<string name="district_11530">Guro-gu</string>
<!-- ja -->
<string name="district_11530">クロ区</string>
```

### district_11545

```xml
<!-- ko -->
<string name="district_11545">금천구</string>
<!-- en -->
<string name="district_11545">Geumcheon-gu</string>
<!-- ja -->
<string name="district_11545">クムチョン区</string>
```

### district_11560

```xml
<!-- ko -->
<string name="district_11560">영등포구</string>
<!-- en -->
<string name="district_11560">Yeongdeungpo-gu</string>
<!-- ja -->
<string name="district_11560">ヨンドゥンポ区</string>
```

### district_11590

```xml
<!-- ko -->
<string name="district_11590">동작구</string>
<!-- en -->
<string name="district_11590">Dongjak-gu</string>
<!-- ja -->
<string name="district_11590">トンジャク区</string>
```

### district_11620

```xml
<!-- ko -->
<string name="district_11620">관악구</string>
<!-- en -->
<string name="district_11620">Gwanak-gu</string>
<!-- ja -->
<string name="district_11620">クァナク区</string>
```

### district_11650

```xml
<!-- ko -->
<string name="district_11650">서초구</string>
<!-- en -->
<string name="district_11650">Seocho-gu</string>
<!-- ja -->
<string name="district_11650">ソチョ区</string>
```

### district_11680

```xml
<!-- ko -->
<string name="district_11680">강남구</string>
<!-- en -->
<string name="district_11680">Gangnam-gu</string>
<!-- ja -->
<string name="district_11680">カンナム区</string>
```

### district_11710

```xml
<!-- ko -->
<string name="district_11710">송파구</string>
<!-- en -->
<string name="district_11710">Songpa-gu</string>
<!-- ja -->
<string name="district_11710">ソンパ区</string>
```

### district_11740

```xml
<!-- ko -->
<string name="district_11740">강동구</string>
<!-- en -->
<string name="district_11740">Gangdong-gu</string>
<!-- ja -->
<string name="district_11740">カンドン区</string>
```

### district_12110

```xml
<!-- ko -->
<string name="district_12110">목포시</string>
<!-- en -->
<string name="district_12110">Mokpo-si</string>
<!-- ja -->
<string name="district_12110">モクポ市</string>
```

### district_12130

```xml
<!-- ko -->
<string name="district_12130">여수시</string>
<!-- en -->
<string name="district_12130">Yeosu-si</string>
<!-- ja -->
<string name="district_12130">ヨス市</string>
```

### district_12150

```xml
<!-- ko -->
<string name="district_12150">순천시</string>
<!-- en -->
<string name="district_12150">Suncheon-si</string>
<!-- ja -->
<string name="district_12150">スンチョン市</string>
```

### district_12170

```xml
<!-- ko -->
<string name="district_12170">나주시</string>
<!-- en -->
<string name="district_12170">Naju-si</string>
<!-- ja -->
<string name="district_12170">ナジュ市</string>
```

### district_12190

```xml
<!-- ko -->
<string name="district_12190">광양시</string>
<!-- en -->
<string name="district_12190">Gwangyang-si</string>
<!-- ja -->
<string name="district_12190">クァンヤン市</string>
```

### district_12210

```xml
<!-- ko -->
<string name="district_12210">동구</string>
<!-- en -->
<string name="district_12210">Dong-gu</string>
<!-- ja -->
<string name="district_12210">トン区</string>
```

### district_12240

```xml
<!-- ko -->
<string name="district_12240">서구</string>
<!-- en -->
<string name="district_12240">Seo-gu</string>
<!-- ja -->
<string name="district_12240">ソ区</string>
```

### district_12270

```xml
<!-- ko -->
<string name="district_12270">남구</string>
<!-- en -->
<string name="district_12270">Nam-gu</string>
<!-- ja -->
<string name="district_12270">ナム区</string>
```

### district_12300

```xml
<!-- ko -->
<string name="district_12300">북구</string>
<!-- en -->
<string name="district_12300">Buk-gu</string>
<!-- ja -->
<string name="district_12300">プク区</string>
```

### district_12330

```xml
<!-- ko -->
<string name="district_12330">광산구</string>
<!-- en -->
<string name="district_12330">Gwangsan-gu</string>
<!-- ja -->
<string name="district_12330">クァンサン区</string>
```

### district_12710

```xml
<!-- ko -->
<string name="district_12710">담양군</string>
<!-- en -->
<string name="district_12710">Damyang-gun</string>
<!-- ja -->
<string name="district_12710">タミャン郡</string>
```

### district_12720

```xml
<!-- ko -->
<string name="district_12720">곡성군</string>
<!-- en -->
<string name="district_12720">Gokseong-gun</string>
<!-- ja -->
<string name="district_12720">コクソン郡</string>
```

### district_12730

```xml
<!-- ko -->
<string name="district_12730">구례군</string>
<!-- en -->
<string name="district_12730">Gurye-gun</string>
<!-- ja -->
<string name="district_12730">クレ郡</string>
```

### district_12740

```xml
<!-- ko -->
<string name="district_12740">고흥군</string>
<!-- en -->
<string name="district_12740">Goheung-gun</string>
<!-- ja -->
<string name="district_12740">コフン郡</string>
```

### district_12750

```xml
<!-- ko -->
<string name="district_12750">보성군</string>
<!-- en -->
<string name="district_12750">Boseong-gun</string>
<!-- ja -->
<string name="district_12750">ポソン郡</string>
```

### district_12760

```xml
<!-- ko -->
<string name="district_12760">화순군</string>
<!-- en -->
<string name="district_12760">Hwasun-gun</string>
<!-- ja -->
<string name="district_12760">ファスン郡</string>
```

### district_12770

```xml
<!-- ko -->
<string name="district_12770">장흥군</string>
<!-- en -->
<string name="district_12770">Jangheung-gun</string>
<!-- ja -->
<string name="district_12770">チャンフン郡</string>
```

### district_12780

```xml
<!-- ko -->
<string name="district_12780">강진군</string>
<!-- en -->
<string name="district_12780">Gangjin-gun</string>
<!-- ja -->
<string name="district_12780">カンジン郡</string>
```

### district_12790

```xml
<!-- ko -->
<string name="district_12790">해남군</string>
<!-- en -->
<string name="district_12790">Haenam-gun</string>
<!-- ja -->
<string name="district_12790">ヘナム郡</string>
```

### district_12800

```xml
<!-- ko -->
<string name="district_12800">영암군</string>
<!-- en -->
<string name="district_12800">Yeongam-gun</string>
<!-- ja -->
<string name="district_12800">ヨンアム郡</string>
```

### district_12810

```xml
<!-- ko -->
<string name="district_12810">무안군</string>
<!-- en -->
<string name="district_12810">Muan-gun</string>
<!-- ja -->
<string name="district_12810">ムアン郡</string>
```

### district_12820

```xml
<!-- ko -->
<string name="district_12820">함평군</string>
<!-- en -->
<string name="district_12820">Hampyeong-gun</string>
<!-- ja -->
<string name="district_12820">ハムピョン郡</string>
```

### district_12830

```xml
<!-- ko -->
<string name="district_12830">영광군</string>
<!-- en -->
<string name="district_12830">Yeonggwang-gun</string>
<!-- ja -->
<string name="district_12830">ヨングァン郡</string>
```

### district_12840

```xml
<!-- ko -->
<string name="district_12840">장성군</string>
<!-- en -->
<string name="district_12840">Jangseong-gun</string>
<!-- ja -->
<string name="district_12840">チャンソン郡</string>
```

### district_12850

```xml
<!-- ko -->
<string name="district_12850">완도군</string>
<!-- en -->
<string name="district_12850">Wando-gun</string>
<!-- ja -->
<string name="district_12850">ワンド郡</string>
```

### district_12860

```xml
<!-- ko -->
<string name="district_12860">진도군</string>
<!-- en -->
<string name="district_12860">Jindo-gun</string>
<!-- ja -->
<string name="district_12860">チンド郡</string>
```

### district_12870

```xml
<!-- ko -->
<string name="district_12870">신안군</string>
<!-- en -->
<string name="district_12870">Sinan-gun</string>
<!-- ja -->
<string name="district_12870">シナン郡</string>
```

### district_26110

```xml
<!-- ko -->
<string name="district_26110">중구</string>
<!-- en -->
<string name="district_26110">Jung-gu</string>
<!-- ja -->
<string name="district_26110">チュン区</string>
```

### district_26140

```xml
<!-- ko -->
<string name="district_26140">서구</string>
<!-- en -->
<string name="district_26140">Seo-gu</string>
<!-- ja -->
<string name="district_26140">ソ区</string>
```

### district_26170

```xml
<!-- ko -->
<string name="district_26170">동구</string>
<!-- en -->
<string name="district_26170">Dong-gu</string>
<!-- ja -->
<string name="district_26170">トン区</string>
```

### district_26200

```xml
<!-- ko -->
<string name="district_26200">영도구</string>
<!-- en -->
<string name="district_26200">Yeongdo-gu</string>
<!-- ja -->
<string name="district_26200">ヨンド区</string>
```

### district_26230

```xml
<!-- ko -->
<string name="district_26230">부산진구</string>
<!-- en -->
<string name="district_26230">Busanjin-gu</string>
<!-- ja -->
<string name="district_26230">プサンジン区</string>
```

### district_26260

```xml
<!-- ko -->
<string name="district_26260">동래구</string>
<!-- en -->
<string name="district_26260">Dongnae-gu</string>
<!-- ja -->
<string name="district_26260">トンネ区</string>
```

### district_26290

```xml
<!-- ko -->
<string name="district_26290">남구</string>
<!-- en -->
<string name="district_26290">Nam-gu</string>
<!-- ja -->
<string name="district_26290">ナム区</string>
```

### district_26320

```xml
<!-- ko -->
<string name="district_26320">북구</string>
<!-- en -->
<string name="district_26320">Buk-gu</string>
<!-- ja -->
<string name="district_26320">プク区</string>
```

### district_26350

```xml
<!-- ko -->
<string name="district_26350">해운대구</string>
<!-- en -->
<string name="district_26350">Haeundae-gu</string>
<!-- ja -->
<string name="district_26350">ヘウンデ区</string>
```

### district_26380

```xml
<!-- ko -->
<string name="district_26380">사하구</string>
<!-- en -->
<string name="district_26380">Saha-gu</string>
<!-- ja -->
<string name="district_26380">サハ区</string>
```

### district_26410

```xml
<!-- ko -->
<string name="district_26410">금정구</string>
<!-- en -->
<string name="district_26410">Geumjeong-gu</string>
<!-- ja -->
<string name="district_26410">クムジョン区</string>
```

### district_26440

```xml
<!-- ko -->
<string name="district_26440">강서구</string>
<!-- en -->
<string name="district_26440">Gangseo-gu</string>
<!-- ja -->
<string name="district_26440">カンソ区</string>
```

### district_26470

```xml
<!-- ko -->
<string name="district_26470">연제구</string>
<!-- en -->
<string name="district_26470">Yeonje-gu</string>
<!-- ja -->
<string name="district_26470">ヨンジェ区</string>
```

### district_26500

```xml
<!-- ko -->
<string name="district_26500">수영구</string>
<!-- en -->
<string name="district_26500">Suyeong-gu</string>
<!-- ja -->
<string name="district_26500">スヨン区</string>
```

### district_26530

```xml
<!-- ko -->
<string name="district_26530">사상구</string>
<!-- en -->
<string name="district_26530">Sasang-gu</string>
<!-- ja -->
<string name="district_26530">ササン区</string>
```

### district_26710

```xml
<!-- ko -->
<string name="district_26710">기장군</string>
<!-- en -->
<string name="district_26710">Gijang-gun</string>
<!-- ja -->
<string name="district_26710">キジャン郡</string>
```

### district_27110

```xml
<!-- ko -->
<string name="district_27110">중구</string>
<!-- en -->
<string name="district_27110">Jung-gu</string>
<!-- ja -->
<string name="district_27110">チュン区</string>
```

### district_27140

```xml
<!-- ko -->
<string name="district_27140">동구</string>
<!-- en -->
<string name="district_27140">Dong-gu</string>
<!-- ja -->
<string name="district_27140">トン区</string>
```

### district_27170

```xml
<!-- ko -->
<string name="district_27170">서구</string>
<!-- en -->
<string name="district_27170">Seo-gu</string>
<!-- ja -->
<string name="district_27170">ソ区</string>
```

### district_27200

```xml
<!-- ko -->
<string name="district_27200">남구</string>
<!-- en -->
<string name="district_27200">Nam-gu</string>
<!-- ja -->
<string name="district_27200">ナム区</string>
```

### district_27230

```xml
<!-- ko -->
<string name="district_27230">북구</string>
<!-- en -->
<string name="district_27230">Buk-gu</string>
<!-- ja -->
<string name="district_27230">プク区</string>
```

### district_27260

```xml
<!-- ko -->
<string name="district_27260">수성구</string>
<!-- en -->
<string name="district_27260">Suseong-gu</string>
<!-- ja -->
<string name="district_27260">スソン区</string>
```

### district_27290

```xml
<!-- ko -->
<string name="district_27290">달서구</string>
<!-- en -->
<string name="district_27290">Dalseo-gu</string>
<!-- ja -->
<string name="district_27290">タルソ区</string>
```

### district_27710

```xml
<!-- ko -->
<string name="district_27710">달성군</string>
<!-- en -->
<string name="district_27710">Dalseong-gun</string>
<!-- ja -->
<string name="district_27710">タルソン郡</string>
```

### district_27720

```xml
<!-- ko -->
<string name="district_27720">군위군</string>
<!-- en -->
<string name="district_27720">Gunwi-gun</string>
<!-- ja -->
<string name="district_27720">クヌィ郡</string>
```

### district_28125

```xml
<!-- ko -->
<string name="district_28125">제물포구</string>
<!-- en -->
<string name="district_28125">Jemulpo-gu</string>
<!-- ja -->
<string name="district_28125">チェムルポ区</string>
```

### district_28155

```xml
<!-- ko -->
<string name="district_28155">영종구</string>
<!-- en -->
<string name="district_28155">Yeongjong-gu</string>
<!-- ja -->
<string name="district_28155">ヨンジョン区</string>
```

### district_28177

```xml
<!-- ko -->
<string name="district_28177">미추홀구</string>
<!-- en -->
<string name="district_28177">Michuhol-gu</string>
<!-- ja -->
<string name="district_28177">ミチュホル区</string>
```

### district_28185

```xml
<!-- ko -->
<string name="district_28185">연수구</string>
<!-- en -->
<string name="district_28185">Yeonsu-gu</string>
<!-- ja -->
<string name="district_28185">ヨンス区</string>
```

### district_28200

```xml
<!-- ko -->
<string name="district_28200">남동구</string>
<!-- en -->
<string name="district_28200">Namdong-gu</string>
<!-- ja -->
<string name="district_28200">ナムドン区</string>
```

### district_28237

```xml
<!-- ko -->
<string name="district_28237">부평구</string>
<!-- en -->
<string name="district_28237">Bupyeong-gu</string>
<!-- ja -->
<string name="district_28237">プピョン区</string>
```

### district_28245

```xml
<!-- ko -->
<string name="district_28245">계양구</string>
<!-- en -->
<string name="district_28245">Gyeyang-gu</string>
<!-- ja -->
<string name="district_28245">ケヤン区</string>
```

### district_28275

```xml
<!-- ko -->
<string name="district_28275">서해구</string>
<!-- en -->
<string name="district_28275">Seohae-gu</string>
<!-- ja -->
<string name="district_28275">ソヘ区</string>
```

### district_28290

```xml
<!-- ko -->
<string name="district_28290">검단구</string>
<!-- en -->
<string name="district_28290">Geomdan-gu</string>
<!-- ja -->
<string name="district_28290">コムダン区</string>
```

### district_28710

```xml
<!-- ko -->
<string name="district_28710">강화군</string>
<!-- en -->
<string name="district_28710">Ganghwa-gun</string>
<!-- ja -->
<string name="district_28710">カンファ郡</string>
```

### district_28720

```xml
<!-- ko -->
<string name="district_28720">옹진군</string>
<!-- en -->
<string name="district_28720">Ongjin-gun</string>
<!-- ja -->
<string name="district_28720">オンジン郡</string>
```

### district_30110

```xml
<!-- ko -->
<string name="district_30110">동구</string>
<!-- en -->
<string name="district_30110">Dong-gu</string>
<!-- ja -->
<string name="district_30110">トン区</string>
```

### district_30140

```xml
<!-- ko -->
<string name="district_30140">중구</string>
<!-- en -->
<string name="district_30140">Jung-gu</string>
<!-- ja -->
<string name="district_30140">チュン区</string>
```

### district_30170

```xml
<!-- ko -->
<string name="district_30170">서구</string>
<!-- en -->
<string name="district_30170">Seo-gu</string>
<!-- ja -->
<string name="district_30170">ソ区</string>
```

### district_30200

```xml
<!-- ko -->
<string name="district_30200">유성구</string>
<!-- en -->
<string name="district_30200">Yuseong-gu</string>
<!-- ja -->
<string name="district_30200">ユソン区</string>
```

### district_30230

```xml
<!-- ko -->
<string name="district_30230">대덕구</string>
<!-- en -->
<string name="district_30230">Daedeok-gu</string>
<!-- ja -->
<string name="district_30230">テドク区</string>
```

### district_31110

```xml
<!-- ko -->
<string name="district_31110">중구</string>
<!-- en -->
<string name="district_31110">Jung-gu</string>
<!-- ja -->
<string name="district_31110">チュン区</string>
```

### district_31140

```xml
<!-- ko -->
<string name="district_31140">남구</string>
<!-- en -->
<string name="district_31140">Nam-gu</string>
<!-- ja -->
<string name="district_31140">ナム区</string>
```

### district_31170

```xml
<!-- ko -->
<string name="district_31170">동구</string>
<!-- en -->
<string name="district_31170">Dong-gu</string>
<!-- ja -->
<string name="district_31170">トン区</string>
```

### district_31200

```xml
<!-- ko -->
<string name="district_31200">북구</string>
<!-- en -->
<string name="district_31200">Buk-gu</string>
<!-- ja -->
<string name="district_31200">プク区</string>
```

### district_31710

```xml
<!-- ko -->
<string name="district_31710">울주군</string>
<!-- en -->
<string name="district_31710">Ulju-gun</string>
<!-- ja -->
<string name="district_31710">ウルジュ郡</string>
```

### district_41111

```xml
<!-- ko -->
<string name="district_41111">수원시 장안구</string>
<!-- en -->
<string name="district_41111">Jangan-gu, Suwon-si</string>
<!-- ja -->
<string name="district_41111">スウォン市 チャンアン区</string>
```

### district_41113

```xml
<!-- ko -->
<string name="district_41113">수원시 권선구</string>
<!-- en -->
<string name="district_41113">Gwonseon-gu, Suwon-si</string>
<!-- ja -->
<string name="district_41113">スウォン市 クォンソン区</string>
```

### district_41115

```xml
<!-- ko -->
<string name="district_41115">수원시 팔달구</string>
<!-- en -->
<string name="district_41115">Paldal-gu, Suwon-si</string>
<!-- ja -->
<string name="district_41115">スウォン市 パルダル区</string>
```

### district_41117

```xml
<!-- ko -->
<string name="district_41117">수원시 영통구</string>
<!-- en -->
<string name="district_41117">Yeongtong-gu, Suwon-si</string>
<!-- ja -->
<string name="district_41117">スウォン市 ヨントン区</string>
```

### district_41131

```xml
<!-- ko -->
<string name="district_41131">성남시 수정구</string>
<!-- en -->
<string name="district_41131">Sujeong-gu, Seongnam-si</string>
<!-- ja -->
<string name="district_41131">ソンナム市 スジョン区</string>
```

### district_41133

```xml
<!-- ko -->
<string name="district_41133">성남시 중원구</string>
<!-- en -->
<string name="district_41133">Jungwon-gu, Seongnam-si</string>
<!-- ja -->
<string name="district_41133">ソンナム市 チュンウォン区</string>
```

### district_41135

```xml
<!-- ko -->
<string name="district_41135">성남시 분당구</string>
<!-- en -->
<string name="district_41135">Bundang-gu, Seongnam-si</string>
<!-- ja -->
<string name="district_41135">ソンナム市 プンダン区</string>
```

### district_41150

```xml
<!-- ko -->
<string name="district_41150">의정부시</string>
<!-- en -->
<string name="district_41150">Uijeongbu-si</string>
<!-- ja -->
<string name="district_41150">ウィジョンブ市</string>
```

### district_41171

```xml
<!-- ko -->
<string name="district_41171">안양시 만안구</string>
<!-- en -->
<string name="district_41171">Manan-gu, Anyang-si</string>
<!-- ja -->
<string name="district_41171">アニャン市 マナン区</string>
```

### district_41173

```xml
<!-- ko -->
<string name="district_41173">안양시 동안구</string>
<!-- en -->
<string name="district_41173">Dongan-gu, Anyang-si</string>
<!-- ja -->
<string name="district_41173">アニャン市 トンアン区</string>
```

### district_41192

```xml
<!-- ko -->
<string name="district_41192">부천시 원미구</string>
<!-- en -->
<string name="district_41192">Wonmi-gu, Bucheon-si</string>
<!-- ja -->
<string name="district_41192">プチョン市 ウォンミ区</string>
```

### district_41194

```xml
<!-- ko -->
<string name="district_41194">부천시 소사구</string>
<!-- en -->
<string name="district_41194">Sosa-gu, Bucheon-si</string>
<!-- ja -->
<string name="district_41194">プチョン市 ソサ区</string>
```

### district_41196

```xml
<!-- ko -->
<string name="district_41196">부천시 오정구</string>
<!-- en -->
<string name="district_41196">Ojeong-gu, Bucheon-si</string>
<!-- ja -->
<string name="district_41196">プチョン市 オジョン区</string>
```

### district_41210

```xml
<!-- ko -->
<string name="district_41210">광명시</string>
<!-- en -->
<string name="district_41210">Gwangmyeong-si</string>
<!-- ja -->
<string name="district_41210">クァンミョン市</string>
```

### district_41220

```xml
<!-- ko -->
<string name="district_41220">평택시</string>
<!-- en -->
<string name="district_41220">Pyeongtaek-si</string>
<!-- ja -->
<string name="district_41220">ピョンテク市</string>
```

### district_41250

```xml
<!-- ko -->
<string name="district_41250">동두천시</string>
<!-- en -->
<string name="district_41250">Dongducheon-si</string>
<!-- ja -->
<string name="district_41250">トンドゥチョン市</string>
```

### district_41271

```xml
<!-- ko -->
<string name="district_41271">안산시 상록구</string>
<!-- en -->
<string name="district_41271">Sangnok-gu, Ansan-si</string>
<!-- ja -->
<string name="district_41271">アンサン市 サンノク区</string>
```

### district_41273

```xml
<!-- ko -->
<string name="district_41273">안산시 단원구</string>
<!-- en -->
<string name="district_41273">Danwon-gu, Ansan-si</string>
<!-- ja -->
<string name="district_41273">アンサン市 タンウォン区</string>
```

### district_41281

```xml
<!-- ko -->
<string name="district_41281">고양시 덕양구</string>
<!-- en -->
<string name="district_41281">Deogyang-gu, Goyang-si</string>
<!-- ja -->
<string name="district_41281">コヤン市 トギャン区</string>
```

### district_41285

```xml
<!-- ko -->
<string name="district_41285">고양시 일산동구</string>
<!-- en -->
<string name="district_41285">Ilsandong-gu, Goyang-si</string>
<!-- ja -->
<string name="district_41285">コヤン市 イルサンドン区</string>
```

### district_41287

```xml
<!-- ko -->
<string name="district_41287">고양시 일산서구</string>
<!-- en -->
<string name="district_41287">Ilsanseo-gu, Goyang-si</string>
<!-- ja -->
<string name="district_41287">コヤン市 イルサンソ区</string>
```

### district_41290

```xml
<!-- ko -->
<string name="district_41290">과천시</string>
<!-- en -->
<string name="district_41290">Gwacheon-si</string>
<!-- ja -->
<string name="district_41290">クァチョン市</string>
```

### district_41310

```xml
<!-- ko -->
<string name="district_41310">구리시</string>
<!-- en -->
<string name="district_41310">Guri-si</string>
<!-- ja -->
<string name="district_41310">クリ市</string>
```

### district_41360

```xml
<!-- ko -->
<string name="district_41360">남양주시</string>
<!-- en -->
<string name="district_41360">Namyangju-si</string>
<!-- ja -->
<string name="district_41360">ナミャンジュ市</string>
```

### district_41370

```xml
<!-- ko -->
<string name="district_41370">오산시</string>
<!-- en -->
<string name="district_41370">Osan-si</string>
<!-- ja -->
<string name="district_41370">オサン市</string>
```

### district_41390

```xml
<!-- ko -->
<string name="district_41390">시흥시</string>
<!-- en -->
<string name="district_41390">Siheung-si</string>
<!-- ja -->
<string name="district_41390">シフン市</string>
```

### district_41410

```xml
<!-- ko -->
<string name="district_41410">군포시</string>
<!-- en -->
<string name="district_41410">Gunpo-si</string>
<!-- ja -->
<string name="district_41410">クンポ市</string>
```

### district_41430

```xml
<!-- ko -->
<string name="district_41430">의왕시</string>
<!-- en -->
<string name="district_41430">Uiwang-si</string>
<!-- ja -->
<string name="district_41430">ウィワン市</string>
```

### district_41450

```xml
<!-- ko -->
<string name="district_41450">하남시</string>
<!-- en -->
<string name="district_41450">Hanam-si</string>
<!-- ja -->
<string name="district_41450">ハナム市</string>
```

### district_41461

```xml
<!-- ko -->
<string name="district_41461">용인시 처인구</string>
<!-- en -->
<string name="district_41461">Cheoin-gu, Yongin-si</string>
<!-- ja -->
<string name="district_41461">ヨンイン市 チョイン区</string>
```

### district_41463

```xml
<!-- ko -->
<string name="district_41463">용인시 기흥구</string>
<!-- en -->
<string name="district_41463">Giheung-gu, Yongin-si</string>
<!-- ja -->
<string name="district_41463">ヨンイン市 キフン区</string>
```

### district_41465

```xml
<!-- ko -->
<string name="district_41465">용인시 수지구</string>
<!-- en -->
<string name="district_41465">Suji-gu, Yongin-si</string>
<!-- ja -->
<string name="district_41465">ヨンイン市 スジ区</string>
```

### district_41480

```xml
<!-- ko -->
<string name="district_41480">파주시</string>
<!-- en -->
<string name="district_41480">Paju-si</string>
<!-- ja -->
<string name="district_41480">パジュ市</string>
```

### district_41500

```xml
<!-- ko -->
<string name="district_41500">이천시</string>
<!-- en -->
<string name="district_41500">Icheon-si</string>
<!-- ja -->
<string name="district_41500">イチョン市</string>
```

### district_41550

```xml
<!-- ko -->
<string name="district_41550">안성시</string>
<!-- en -->
<string name="district_41550">Anseong-si</string>
<!-- ja -->
<string name="district_41550">アンソン市</string>
```

### district_41570

```xml
<!-- ko -->
<string name="district_41570">김포시</string>
<!-- en -->
<string name="district_41570">Gimpo-si</string>
<!-- ja -->
<string name="district_41570">キンポ市</string>
```

### district_41591

```xml
<!-- ko -->
<string name="district_41591">화성시 만세구</string>
<!-- en -->
<string name="district_41591">Manse-gu, Hwaseong-si</string>
<!-- ja -->
<string name="district_41591">ファソン市 マンセ区</string>
```

### district_41593

```xml
<!-- ko -->
<string name="district_41593">화성시 효행구</string>
<!-- en -->
<string name="district_41593">Hyohaeng-gu, Hwaseong-si</string>
<!-- ja -->
<string name="district_41593">ファソン市 ヒョヘン区</string>
```

### district_41595

```xml
<!-- ko -->
<string name="district_41595">화성시 병점구</string>
<!-- en -->
<string name="district_41595">Byeongjeom-gu, Hwaseong-si</string>
<!-- ja -->
<string name="district_41595">ファソン市 ピョンジョム区</string>
```

### district_41597

```xml
<!-- ko -->
<string name="district_41597">화성시 동탄구</string>
<!-- en -->
<string name="district_41597">Dongtan-gu, Hwaseong-si</string>
<!-- ja -->
<string name="district_41597">ファソン市 トンタン区</string>
```

### district_41610

```xml
<!-- ko -->
<string name="district_41610">광주시</string>
<!-- en -->
<string name="district_41610">Gwangju-si</string>
<!-- ja -->
<string name="district_41610">クァンジュ市</string>
```

### district_41630

```xml
<!-- ko -->
<string name="district_41630">양주시</string>
<!-- en -->
<string name="district_41630">Yangju-si</string>
<!-- ja -->
<string name="district_41630">ヤンジュ市</string>
```

### district_41650

```xml
<!-- ko -->
<string name="district_41650">포천시</string>
<!-- en -->
<string name="district_41650">Pocheon-si</string>
<!-- ja -->
<string name="district_41650">ポチョン市</string>
```

### district_41670

```xml
<!-- ko -->
<string name="district_41670">여주시</string>
<!-- en -->
<string name="district_41670">Yeoju-si</string>
<!-- ja -->
<string name="district_41670">ヨジュ市</string>
```

### district_41800

```xml
<!-- ko -->
<string name="district_41800">연천군</string>
<!-- en -->
<string name="district_41800">Yeoncheon-gun</string>
<!-- ja -->
<string name="district_41800">ヨンチョン郡</string>
```

### district_41820

```xml
<!-- ko -->
<string name="district_41820">가평군</string>
<!-- en -->
<string name="district_41820">Gapyeong-gun</string>
<!-- ja -->
<string name="district_41820">カピョン郡</string>
```

### district_41830

```xml
<!-- ko -->
<string name="district_41830">양평군</string>
<!-- en -->
<string name="district_41830">Yangpyeong-gun</string>
<!-- ja -->
<string name="district_41830">ヤンピョン郡</string>
```

### district_43111

```xml
<!-- ko -->
<string name="district_43111">청주시 상당구</string>
<!-- en -->
<string name="district_43111">Sangdang-gu, Cheongju-si</string>
<!-- ja -->
<string name="district_43111">チョンジュ市 サンダン区</string>
```

### district_43112

```xml
<!-- ko -->
<string name="district_43112">청주시 서원구</string>
<!-- en -->
<string name="district_43112">Seowon-gu, Cheongju-si</string>
<!-- ja -->
<string name="district_43112">チョンジュ市 ソウォン区</string>
```

### district_43113

```xml
<!-- ko -->
<string name="district_43113">청주시 흥덕구</string>
<!-- en -->
<string name="district_43113">Heungdeok-gu, Cheongju-si</string>
<!-- ja -->
<string name="district_43113">チョンジュ市 フンドク区</string>
```

### district_43114

```xml
<!-- ko -->
<string name="district_43114">청주시 청원구</string>
<!-- en -->
<string name="district_43114">Cheongwon-gu, Cheongju-si</string>
<!-- ja -->
<string name="district_43114">チョンジュ市 チョンウォン区</string>
```

### district_43130

```xml
<!-- ko -->
<string name="district_43130">충주시</string>
<!-- en -->
<string name="district_43130">Chungju-si</string>
<!-- ja -->
<string name="district_43130">チュンジュ市</string>
```

### district_43150

```xml
<!-- ko -->
<string name="district_43150">제천시</string>
<!-- en -->
<string name="district_43150">Jecheon-si</string>
<!-- ja -->
<string name="district_43150">チェチョン市</string>
```

### district_43720

```xml
<!-- ko -->
<string name="district_43720">보은군</string>
<!-- en -->
<string name="district_43720">Boeun-gun</string>
<!-- ja -->
<string name="district_43720">ポウン郡</string>
```

### district_43730

```xml
<!-- ko -->
<string name="district_43730">옥천군</string>
<!-- en -->
<string name="district_43730">Okcheon-gun</string>
<!-- ja -->
<string name="district_43730">オクチョン郡</string>
```

### district_43740

```xml
<!-- ko -->
<string name="district_43740">영동군</string>
<!-- en -->
<string name="district_43740">Yeongdong-gun</string>
<!-- ja -->
<string name="district_43740">ヨンドン郡</string>
```

### district_43745

```xml
<!-- ko -->
<string name="district_43745">증평군</string>
<!-- en -->
<string name="district_43745">Jeungpyeong-gun</string>
<!-- ja -->
<string name="district_43745">チュンピョン郡</string>
```

### district_43750

```xml
<!-- ko -->
<string name="district_43750">진천군</string>
<!-- en -->
<string name="district_43750">Jincheon-gun</string>
<!-- ja -->
<string name="district_43750">チンチョン郡</string>
```

### district_43760

```xml
<!-- ko -->
<string name="district_43760">괴산군</string>
<!-- en -->
<string name="district_43760">Goesan-gun</string>
<!-- ja -->
<string name="district_43760">クェサン郡</string>
```

### district_43770

```xml
<!-- ko -->
<string name="district_43770">음성군</string>
<!-- en -->
<string name="district_43770">Eumseong-gun</string>
<!-- ja -->
<string name="district_43770">ウムソン郡</string>
```

### district_43800

```xml
<!-- ko -->
<string name="district_43800">단양군</string>
<!-- en -->
<string name="district_43800">Danyang-gun</string>
<!-- ja -->
<string name="district_43800">タニャン郡</string>
```

### district_44131

```xml
<!-- ko -->
<string name="district_44131">천안시 동남구</string>
<!-- en -->
<string name="district_44131">Dongnam-gu, Cheonan-si</string>
<!-- ja -->
<string name="district_44131">チョナン市 トンナム区</string>
```

### district_44133

```xml
<!-- ko -->
<string name="district_44133">천안시 서북구</string>
<!-- en -->
<string name="district_44133">Seobuk-gu, Cheonan-si</string>
<!-- ja -->
<string name="district_44133">チョナン市 ソブク区</string>
```

### district_44150

```xml
<!-- ko -->
<string name="district_44150">공주시</string>
<!-- en -->
<string name="district_44150">Gongju-si</string>
<!-- ja -->
<string name="district_44150">コンジュ市</string>
```

### district_44180

```xml
<!-- ko -->
<string name="district_44180">보령시</string>
<!-- en -->
<string name="district_44180">Boryeong-si</string>
<!-- ja -->
<string name="district_44180">ポリョン市</string>
```

### district_44200

```xml
<!-- ko -->
<string name="district_44200">아산시</string>
<!-- en -->
<string name="district_44200">Asan-si</string>
<!-- ja -->
<string name="district_44200">アサン市</string>
```

### district_44210

```xml
<!-- ko -->
<string name="district_44210">서산시</string>
<!-- en -->
<string name="district_44210">Seosan-si</string>
<!-- ja -->
<string name="district_44210">ソサン市</string>
```

### district_44230

```xml
<!-- ko -->
<string name="district_44230">논산시</string>
<!-- en -->
<string name="district_44230">Nonsan-si</string>
<!-- ja -->
<string name="district_44230">ノンサン市</string>
```

### district_44250

```xml
<!-- ko -->
<string name="district_44250">계룡시</string>
<!-- en -->
<string name="district_44250">Gyeryong-si</string>
<!-- ja -->
<string name="district_44250">ケリョン市</string>
```

### district_44270

```xml
<!-- ko -->
<string name="district_44270">당진시</string>
<!-- en -->
<string name="district_44270">Dangjin-si</string>
<!-- ja -->
<string name="district_44270">タンジン市</string>
```

### district_44710

```xml
<!-- ko -->
<string name="district_44710">금산군</string>
<!-- en -->
<string name="district_44710">Geumsan-gun</string>
<!-- ja -->
<string name="district_44710">クムサン郡</string>
```

### district_44760

```xml
<!-- ko -->
<string name="district_44760">부여군</string>
<!-- en -->
<string name="district_44760">Buyeo-gun</string>
<!-- ja -->
<string name="district_44760">プヨ郡</string>
```

### district_44770

```xml
<!-- ko -->
<string name="district_44770">서천군</string>
<!-- en -->
<string name="district_44770">Seocheon-gun</string>
<!-- ja -->
<string name="district_44770">ソチョン郡</string>
```

### district_44790

```xml
<!-- ko -->
<string name="district_44790">청양군</string>
<!-- en -->
<string name="district_44790">Cheongyang-gun</string>
<!-- ja -->
<string name="district_44790">チョンヤン郡</string>
```

### district_44800

```xml
<!-- ko -->
<string name="district_44800">홍성군</string>
<!-- en -->
<string name="district_44800">Hongseong-gun</string>
<!-- ja -->
<string name="district_44800">ホンソン郡</string>
```

### district_44810

```xml
<!-- ko -->
<string name="district_44810">예산군</string>
<!-- en -->
<string name="district_44810">Yesan-gun</string>
<!-- ja -->
<string name="district_44810">イェサン郡</string>
```

### district_44825

```xml
<!-- ko -->
<string name="district_44825">태안군</string>
<!-- en -->
<string name="district_44825">Taean-gun</string>
<!-- ja -->
<string name="district_44825">テアン郡</string>
```

### district_47111

```xml
<!-- ko -->
<string name="district_47111">포항시 남구</string>
<!-- en -->
<string name="district_47111">Nam-gu, Pohang-si</string>
<!-- ja -->
<string name="district_47111">ポハン市 ナム区</string>
```

### district_47113

```xml
<!-- ko -->
<string name="district_47113">포항시 북구</string>
<!-- en -->
<string name="district_47113">Buk-gu, Pohang-si</string>
<!-- ja -->
<string name="district_47113">ポハン市 プク区</string>
```

### district_47130

```xml
<!-- ko -->
<string name="district_47130">경주시</string>
<!-- en -->
<string name="district_47130">Gyeongju-si</string>
<!-- ja -->
<string name="district_47130">キョンジュ市</string>
```

### district_47150

```xml
<!-- ko -->
<string name="district_47150">김천시</string>
<!-- en -->
<string name="district_47150">Gimcheon-si</string>
<!-- ja -->
<string name="district_47150">キムチョン市</string>
```

### district_47170

```xml
<!-- ko -->
<string name="district_47170">안동시</string>
<!-- en -->
<string name="district_47170">Andong-si</string>
<!-- ja -->
<string name="district_47170">アンドン市</string>
```

### district_47190

```xml
<!-- ko -->
<string name="district_47190">구미시</string>
<!-- en -->
<string name="district_47190">Gumi-si</string>
<!-- ja -->
<string name="district_47190">クミ市</string>
```

### district_47210

```xml
<!-- ko -->
<string name="district_47210">영주시</string>
<!-- en -->
<string name="district_47210">Yeongju-si</string>
<!-- ja -->
<string name="district_47210">ヨンジュ市</string>
```

### district_47230

```xml
<!-- ko -->
<string name="district_47230">영천시</string>
<!-- en -->
<string name="district_47230">Yeongcheon-si</string>
<!-- ja -->
<string name="district_47230">ヨンチョン市</string>
```

### district_47250

```xml
<!-- ko -->
<string name="district_47250">상주시</string>
<!-- en -->
<string name="district_47250">Sangju-si</string>
<!-- ja -->
<string name="district_47250">サンジュ市</string>
```

### district_47280

```xml
<!-- ko -->
<string name="district_47280">문경시</string>
<!-- en -->
<string name="district_47280">Mungyeong-si</string>
<!-- ja -->
<string name="district_47280">ムンギョン市</string>
```

### district_47290

```xml
<!-- ko -->
<string name="district_47290">경산시</string>
<!-- en -->
<string name="district_47290">Gyeongsan-si</string>
<!-- ja -->
<string name="district_47290">キョンサン市</string>
```

### district_47730

```xml
<!-- ko -->
<string name="district_47730">의성군</string>
<!-- en -->
<string name="district_47730">Uiseong-gun</string>
<!-- ja -->
<string name="district_47730">ウィソン郡</string>
```

### district_47750

```xml
<!-- ko -->
<string name="district_47750">청송군</string>
<!-- en -->
<string name="district_47750">Cheongsong-gun</string>
<!-- ja -->
<string name="district_47750">チョンソン郡</string>
```

### district_47760

```xml
<!-- ko -->
<string name="district_47760">영양군</string>
<!-- en -->
<string name="district_47760">Yeongyang-gun</string>
<!-- ja -->
<string name="district_47760">ヨンヤン郡</string>
```

### district_47770

```xml
<!-- ko -->
<string name="district_47770">영덕군</string>
<!-- en -->
<string name="district_47770">Yeongdeok-gun</string>
<!-- ja -->
<string name="district_47770">ヨンドク郡</string>
```

### district_47820

```xml
<!-- ko -->
<string name="district_47820">청도군</string>
<!-- en -->
<string name="district_47820">Cheongdo-gun</string>
<!-- ja -->
<string name="district_47820">チョンド郡</string>
```

### district_47830

```xml
<!-- ko -->
<string name="district_47830">고령군</string>
<!-- en -->
<string name="district_47830">Goryeong-gun</string>
<!-- ja -->
<string name="district_47830">コリョン郡</string>
```

### district_47840

```xml
<!-- ko -->
<string name="district_47840">성주군</string>
<!-- en -->
<string name="district_47840">Seongju-gun</string>
<!-- ja -->
<string name="district_47840">ソンジュ郡</string>
```

### district_47850

```xml
<!-- ko -->
<string name="district_47850">칠곡군</string>
<!-- en -->
<string name="district_47850">Chilgok-gun</string>
<!-- ja -->
<string name="district_47850">チルゴク郡</string>
```

### district_47900

```xml
<!-- ko -->
<string name="district_47900">예천군</string>
<!-- en -->
<string name="district_47900">Yecheon-gun</string>
<!-- ja -->
<string name="district_47900">イェチョン郡</string>
```

### district_47920

```xml
<!-- ko -->
<string name="district_47920">봉화군</string>
<!-- en -->
<string name="district_47920">Bonghwa-gun</string>
<!-- ja -->
<string name="district_47920">ポンファ郡</string>
```

### district_47930

```xml
<!-- ko -->
<string name="district_47930">울진군</string>
<!-- en -->
<string name="district_47930">Uljin-gun</string>
<!-- ja -->
<string name="district_47930">ウルチン郡</string>
```

### district_47940

```xml
<!-- ko -->
<string name="district_47940">울릉군</string>
<!-- en -->
<string name="district_47940">Ulleung-gun</string>
<!-- ja -->
<string name="district_47940">ウルルン郡</string>
```

### district_48121

```xml
<!-- ko -->
<string name="district_48121">창원시 의창구</string>
<!-- en -->
<string name="district_48121">Uichang-gu, Changwon-si</string>
<!-- ja -->
<string name="district_48121">チャンウォン市 ウィチャン区</string>
```

### district_48123

```xml
<!-- ko -->
<string name="district_48123">창원시 성산구</string>
<!-- en -->
<string name="district_48123">Seongsan-gu, Changwon-si</string>
<!-- ja -->
<string name="district_48123">チャンウォン市 ソンサン区</string>
```

### district_48125

```xml
<!-- ko -->
<string name="district_48125">창원시 마산합포구</string>
<!-- en -->
<string name="district_48125">Masanhappo-gu, Changwon-si</string>
<!-- ja -->
<string name="district_48125">チャンウォン市 マサンハッポ区</string>
```

### district_48127

```xml
<!-- ko -->
<string name="district_48127">창원시 마산회원구</string>
<!-- en -->
<string name="district_48127">Masanhoewon-gu, Changwon-si</string>
<!-- ja -->
<string name="district_48127">チャンウォン市 マサンフェウォン区</string>
```

### district_48129

```xml
<!-- ko -->
<string name="district_48129">창원시 진해구</string>
<!-- en -->
<string name="district_48129">Jinhae-gu, Changwon-si</string>
<!-- ja -->
<string name="district_48129">チャンウォン市 チネ区</string>
```

### district_48170

```xml
<!-- ko -->
<string name="district_48170">진주시</string>
<!-- en -->
<string name="district_48170">Jinju-si</string>
<!-- ja -->
<string name="district_48170">チンジュ市</string>
```

### district_48220

```xml
<!-- ko -->
<string name="district_48220">통영시</string>
<!-- en -->
<string name="district_48220">Tongyeong-si</string>
<!-- ja -->
<string name="district_48220">トンヨン市</string>
```

### district_48240

```xml
<!-- ko -->
<string name="district_48240">사천시</string>
<!-- en -->
<string name="district_48240">Sacheon-si</string>
<!-- ja -->
<string name="district_48240">サチョン市</string>
```

### district_48250

```xml
<!-- ko -->
<string name="district_48250">김해시</string>
<!-- en -->
<string name="district_48250">Gimhae-si</string>
<!-- ja -->
<string name="district_48250">キメ市</string>
```

### district_48270

```xml
<!-- ko -->
<string name="district_48270">밀양시</string>
<!-- en -->
<string name="district_48270">Miryang-si</string>
<!-- ja -->
<string name="district_48270">ミリャン市</string>
```

### district_48310

```xml
<!-- ko -->
<string name="district_48310">거제시</string>
<!-- en -->
<string name="district_48310">Geoje-si</string>
<!-- ja -->
<string name="district_48310">コジェ市</string>
```

### district_48330

```xml
<!-- ko -->
<string name="district_48330">양산시</string>
<!-- en -->
<string name="district_48330">Yangsan-si</string>
<!-- ja -->
<string name="district_48330">ヤンサン市</string>
```

### district_48720

```xml
<!-- ko -->
<string name="district_48720">의령군</string>
<!-- en -->
<string name="district_48720">Uiryeong-gun</string>
<!-- ja -->
<string name="district_48720">ウィリョン郡</string>
```

### district_48730

```xml
<!-- ko -->
<string name="district_48730">함안군</string>
<!-- en -->
<string name="district_48730">Haman-gun</string>
<!-- ja -->
<string name="district_48730">ハマン郡</string>
```

### district_48740

```xml
<!-- ko -->
<string name="district_48740">창녕군</string>
<!-- en -->
<string name="district_48740">Changnyeong-gun</string>
<!-- ja -->
<string name="district_48740">チャンニョン郡</string>
```

### district_48820

```xml
<!-- ko -->
<string name="district_48820">고성군</string>
<!-- en -->
<string name="district_48820">Goseong-gun</string>
<!-- ja -->
<string name="district_48820">コソン郡</string>
```

### district_48840

```xml
<!-- ko -->
<string name="district_48840">남해군</string>
<!-- en -->
<string name="district_48840">Namhae-gun</string>
<!-- ja -->
<string name="district_48840">ナメ郡</string>
```

### district_48850

```xml
<!-- ko -->
<string name="district_48850">하동군</string>
<!-- en -->
<string name="district_48850">Hadong-gun</string>
<!-- ja -->
<string name="district_48850">ハドン郡</string>
```

### district_48860

```xml
<!-- ko -->
<string name="district_48860">산청군</string>
<!-- en -->
<string name="district_48860">Sancheong-gun</string>
<!-- ja -->
<string name="district_48860">サンチョン郡</string>
```

### district_48870

```xml
<!-- ko -->
<string name="district_48870">함양군</string>
<!-- en -->
<string name="district_48870">Hamyang-gun</string>
<!-- ja -->
<string name="district_48870">ハミャン郡</string>
```

### district_48880

```xml
<!-- ko -->
<string name="district_48880">거창군</string>
<!-- en -->
<string name="district_48880">Geochang-gun</string>
<!-- ja -->
<string name="district_48880">コチャン郡</string>
```

### district_48890

```xml
<!-- ko -->
<string name="district_48890">합천군</string>
<!-- en -->
<string name="district_48890">Hapcheon-gun</string>
<!-- ja -->
<string name="district_48890">ハプチョン郡</string>
```

### district_50110

```xml
<!-- ko -->
<string name="district_50110">제주시</string>
<!-- en -->
<string name="district_50110">Jeju-si</string>
<!-- ja -->
<string name="district_50110">チェジュ市</string>
```

### district_50130

```xml
<!-- ko -->
<string name="district_50130">서귀포시</string>
<!-- en -->
<string name="district_50130">Seogwipo-si</string>
<!-- ja -->
<string name="district_50130">ソグィポ市</string>
```

### district_51110

```xml
<!-- ko -->
<string name="district_51110">춘천시</string>
<!-- en -->
<string name="district_51110">Chuncheon-si</string>
<!-- ja -->
<string name="district_51110">チュンチョン市</string>
```

### district_51130

```xml
<!-- ko -->
<string name="district_51130">원주시</string>
<!-- en -->
<string name="district_51130">Wonju-si</string>
<!-- ja -->
<string name="district_51130">ウォンジュ市</string>
```

### district_51150

```xml
<!-- ko -->
<string name="district_51150">강릉시</string>
<!-- en -->
<string name="district_51150">Gangneung-si</string>
<!-- ja -->
<string name="district_51150">カンヌン市</string>
```

### district_51170

```xml
<!-- ko -->
<string name="district_51170">동해시</string>
<!-- en -->
<string name="district_51170">Donghae-si</string>
<!-- ja -->
<string name="district_51170">トンヘ市</string>
```

### district_51190

```xml
<!-- ko -->
<string name="district_51190">태백시</string>
<!-- en -->
<string name="district_51190">Taebaek-si</string>
<!-- ja -->
<string name="district_51190">テベク市</string>
```

### district_51210

```xml
<!-- ko -->
<string name="district_51210">속초시</string>
<!-- en -->
<string name="district_51210">Sokcho-si</string>
<!-- ja -->
<string name="district_51210">ソクチョ市</string>
```

### district_51230

```xml
<!-- ko -->
<string name="district_51230">삼척시</string>
<!-- en -->
<string name="district_51230">Samcheok-si</string>
<!-- ja -->
<string name="district_51230">サムチョク市</string>
```

### district_51720

```xml
<!-- ko -->
<string name="district_51720">홍천군</string>
<!-- en -->
<string name="district_51720">Hongcheon-gun</string>
<!-- ja -->
<string name="district_51720">ホンチョン郡</string>
```

### district_51730

```xml
<!-- ko -->
<string name="district_51730">횡성군</string>
<!-- en -->
<string name="district_51730">Hoengseong-gun</string>
<!-- ja -->
<string name="district_51730">フェンソン郡</string>
```

### district_51750

```xml
<!-- ko -->
<string name="district_51750">영월군</string>
<!-- en -->
<string name="district_51750">Yeongwol-gun</string>
<!-- ja -->
<string name="district_51750">ヨンウォル郡</string>
```

### district_51760

```xml
<!-- ko -->
<string name="district_51760">평창군</string>
<!-- en -->
<string name="district_51760">Pyeongchang-gun</string>
<!-- ja -->
<string name="district_51760">ピョンチャン郡</string>
```

### district_51770

```xml
<!-- ko -->
<string name="district_51770">정선군</string>
<!-- en -->
<string name="district_51770">Jeongseon-gun</string>
<!-- ja -->
<string name="district_51770">チョンソン郡</string>
```

### district_51780

```xml
<!-- ko -->
<string name="district_51780">철원군</string>
<!-- en -->
<string name="district_51780">Cheorwon-gun</string>
<!-- ja -->
<string name="district_51780">チョルウォン郡</string>
```

### district_51790

```xml
<!-- ko -->
<string name="district_51790">화천군</string>
<!-- en -->
<string name="district_51790">Hwacheon-gun</string>
<!-- ja -->
<string name="district_51790">ファチョン郡</string>
```

### district_51800

```xml
<!-- ko -->
<string name="district_51800">양구군</string>
<!-- en -->
<string name="district_51800">Yanggu-gun</string>
<!-- ja -->
<string name="district_51800">ヤング郡</string>
```

### district_51810

```xml
<!-- ko -->
<string name="district_51810">인제군</string>
<!-- en -->
<string name="district_51810">Inje-gun</string>
<!-- ja -->
<string name="district_51810">インジェ郡</string>
```

### district_51820

```xml
<!-- ko -->
<string name="district_51820">고성군</string>
<!-- en -->
<string name="district_51820">Goseong-gun</string>
<!-- ja -->
<string name="district_51820">コソン郡</string>
```

### district_51830

```xml
<!-- ko -->
<string name="district_51830">양양군</string>
<!-- en -->
<string name="district_51830">Yangyang-gun</string>
<!-- ja -->
<string name="district_51830">ヤンヤン郡</string>
```

### district_52111

```xml
<!-- ko -->
<string name="district_52111">전주시 완산구</string>
<!-- en -->
<string name="district_52111">Wansan-gu, Jeonju-si</string>
<!-- ja -->
<string name="district_52111">チョンジュ市 ワンサン区</string>
```

### district_52113

```xml
<!-- ko -->
<string name="district_52113">전주시 덕진구</string>
<!-- en -->
<string name="district_52113">Deokjin-gu, Jeonju-si</string>
<!-- ja -->
<string name="district_52113">チョンジュ市 トクチン区</string>
```

### district_52130

```xml
<!-- ko -->
<string name="district_52130">군산시</string>
<!-- en -->
<string name="district_52130">Gunsan-si</string>
<!-- ja -->
<string name="district_52130">クンサン市</string>
```

### district_52140

```xml
<!-- ko -->
<string name="district_52140">익산시</string>
<!-- en -->
<string name="district_52140">Iksan-si</string>
<!-- ja -->
<string name="district_52140">イクサン市</string>
```

### district_52180

```xml
<!-- ko -->
<string name="district_52180">정읍시</string>
<!-- en -->
<string name="district_52180">Jeongeup-si</string>
<!-- ja -->
<string name="district_52180">チョンウプ市</string>
```

### district_52190

```xml
<!-- ko -->
<string name="district_52190">남원시</string>
<!-- en -->
<string name="district_52190">Namwon-si</string>
<!-- ja -->
<string name="district_52190">ナムウォン市</string>
```

### district_52210

```xml
<!-- ko -->
<string name="district_52210">김제시</string>
<!-- en -->
<string name="district_52210">Gimje-si</string>
<!-- ja -->
<string name="district_52210">キムジェ市</string>
```

### district_52710

```xml
<!-- ko -->
<string name="district_52710">완주군</string>
<!-- en -->
<string name="district_52710">Wanju-gun</string>
<!-- ja -->
<string name="district_52710">ワンジュ郡</string>
```

### district_52720

```xml
<!-- ko -->
<string name="district_52720">진안군</string>
<!-- en -->
<string name="district_52720">Jinan-gun</string>
<!-- ja -->
<string name="district_52720">チナン郡</string>
```

### district_52730

```xml
<!-- ko -->
<string name="district_52730">무주군</string>
<!-- en -->
<string name="district_52730">Muju-gun</string>
<!-- ja -->
<string name="district_52730">ムジュ郡</string>
```

### district_52740

```xml
<!-- ko -->
<string name="district_52740">장수군</string>
<!-- en -->
<string name="district_52740">Jangsu-gun</string>
<!-- ja -->
<string name="district_52740">チャンス郡</string>
```

### district_52750

```xml
<!-- ko -->
<string name="district_52750">임실군</string>
<!-- en -->
<string name="district_52750">Imsil-gun</string>
<!-- ja -->
<string name="district_52750">イムシル郡</string>
```

### district_52770

```xml
<!-- ko -->
<string name="district_52770">순창군</string>
<!-- en -->
<string name="district_52770">Sunchang-gun</string>
<!-- ja -->
<string name="district_52770">スンチャン郡</string>
```

### district_52790

```xml
<!-- ko -->
<string name="district_52790">고창군</string>
<!-- en -->
<string name="district_52790">Gochang-gun</string>
<!-- ja -->
<string name="district_52790">コチャン郡</string>
```

### district_52800

```xml
<!-- ko -->
<string name="district_52800">부안군</string>
<!-- en -->
<string name="district_52800">Buan-gun</string>
<!-- ja -->
<string name="district_52800">プアン郡</string>
```

## main/presentation/src/main/res/values/region_filter.xml

### main_region_filter_all

```xml
<!-- ko -->
<string name="main_region_filter_all">전체 지역</string>
<!-- en -->
<string name="main_region_filter_all">All areas</string>
<!-- ja -->
<string name="main_region_filter_all">すべての地域</string>
```

### main_region_filter_collapsed

```xml
<!-- ko -->
<string name="main_region_filter_collapsed">지역 목록 접힘</string>
<!-- en -->
<string name="main_region_filter_collapsed">Area list collapsed</string>
<!-- ja -->
<string name="main_region_filter_collapsed">地域一覧を折りたたみ中</string>
```

### main_region_filter_empty

```xml
<!-- ko -->
<string name="main_region_filter_empty">선택할 세부 지역이 없어요</string>
<!-- en -->
<string name="main_region_filter_empty">No smaller areas to select</string>
<!-- ja -->
<string name="main_region_filter_empty">選択できる下位地域はありません</string>
```

### main_region_filter_error

```xml
<!-- ko -->
<string name="main_region_filter_error">지역 목록을 불러오지 못했어요</string>
<!-- en -->
<string name="main_region_filter_error">Unable to load areas</string>
<!-- ja -->
<string name="main_region_filter_error">地域を読み込めませんでした</string>
```

### main_region_filter_expanded

```xml
<!-- ko -->
<string name="main_region_filter_expanded">지역 목록 펼쳐짐</string>
<!-- en -->
<string name="main_region_filter_expanded">Area list expanded</string>
<!-- ja -->
<string name="main_region_filter_expanded">地域一覧を展開中</string>
```

### main_region_filter_label

```xml
<!-- ko -->
<string name="main_region_filter_label">시·구·군 선택</string>
<!-- en -->
<string name="main_region_filter_label">Choose city or district</string>
<!-- ja -->
<string name="main_region_filter_label">市・区・郡を選択</string>
```

### main_region_filter_loading

```xml
<!-- ko -->
<string name="main_region_filter_loading">지역 목록을 불러오는 중이에요</string>
<!-- en -->
<string name="main_region_filter_loading">Loading areas…</string>
<!-- ja -->
<string name="main_region_filter_loading">地域を読み込み中…</string>
```

## main/presentation/src/main/res/values/strings.xml

### language_description

```xml
<!-- ko -->
<string name="language_description">한국어가 아니면 코스 설명 등에서 번역 보기를 사용할 수 있어요. 번역은 기기에서 처리돼요.</string>
<!-- en -->
<string name="language_description">If the language isn\'t Korean, you can translate course descriptions and more. Translation runs on your device.</string>
<!-- ja -->
<string name="language_description">韓国語以外では、コース説明などで翻訳を利用できます。翻訳は端末内で処理されます。</string>
```

### language_selected

```xml
<!-- ko -->
<string name="language_selected">선택됨</string>
<!-- en -->
<string name="language_selected">Selected</string>
<!-- ja -->
<string name="language_selected">選択中</string>
```

### main_account_manage_logout

```xml
<!-- ko -->
<string name="main_account_manage_logout">로그아웃</string>
<!-- en -->
<string name="main_account_manage_logout">Log out</string>
<!-- ja -->
<string name="main_account_manage_logout">ログアウト</string>
```

### main_account_manage_title

```xml
<!-- ko -->
<string name="main_account_manage_title">계정 관리</string>
<!-- en -->
<string name="main_account_manage_title">Account</string>
<!-- ja -->
<string name="main_account_manage_title">アカウント管理</string>
```

### main_account_manage_withdraw

```xml
<!-- ko -->
<string name="main_account_manage_withdraw">회원 탈퇴</string>
<!-- en -->
<string name="main_account_manage_withdraw">Delete account</string>
<!-- ja -->
<string name="main_account_manage_withdraw">退会</string>
```

### main_account_manage_withdraw_dialog_description

```xml
<!-- ko -->
<string name="main_account_manage_withdraw_dialog_description">탈퇴하면 계정과 코스 정보가 삭제되고 되돌릴 수 없어요.</string>
<!-- en -->
<string name="main_account_manage_withdraw_dialog_description">Deleting your account permanently removes your account and course data. This can\'t be undone.</string>
<!-- ja -->
<string name="main_account_manage_withdraw_dialog_description">退会するとアカウントとコース情報が削除され、元に戻せません。</string>
```

### main_account_manage_withdraw_dialog_title

```xml
<!-- ko -->
<string name="main_account_manage_withdraw_dialog_title">정말 탈퇴할까요?</string>
<!-- en -->
<string name="main_account_manage_withdraw_dialog_title">Delete your account?</string>
<!-- ja -->
<string name="main_account_manage_withdraw_dialog_title">本当に退会しますか？</string>
```

### main_app_update_required_cta

```xml
<!-- ko -->
<string name="main_app_update_required_cta">업데이트하러 가기</string>
<!-- en -->
<string name="main_app_update_required_cta">Go to update</string>
<!-- ja -->
<string name="main_app_update_required_cta">更新する</string>
```

### main_app_update_required_message

```xml
<!-- ko -->
<string name="main_app_update_required_message">스토어에서 최신 버전으로 업데이트해 주세요.</string>
<!-- en -->
<string name="main_app_update_required_message">Update to the latest version from the store.</string>
<!-- ja -->
<string name="main_app_update_required_message">ストアから最新バージョンに更新してください。</string>
```

### main_app_update_required_title

```xml
<!-- ko -->
<string name="main_app_update_required_title">새 버전으로 업데이트해 주세요</string>
<!-- en -->
<string name="main_app_update_required_title">Please update to the latest version</string>
<!-- ja -->
<string name="main_app_update_required_title">最新バージョンに更新してください</string>
```

### main_app_update_store_open_failed

```xml
<!-- ko -->
<string name="main_app_update_store_open_failed">스토어를 열 수 없어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_app_update_store_open_failed">Couldn\'t open the store. Please try again later.</string>
<!-- ja -->
<string name="main_app_update_store_open_failed">ストアを開けませんでした。しばらくしてからもう一度お試しください。</string>
```

### main_app_version_current_label

```xml
<!-- ko -->
<string name="main_app_version_current_label">현재 버전</string>
<!-- en -->
<string name="main_app_version_current_label">Current version</string>
<!-- ja -->
<string name="main_app_version_current_label">現在のバージョン</string>
```

### main_app_version_go_update

```xml
<!-- ko -->
<string name="main_app_version_go_update">업데이트하러 가기</string>
<!-- en -->
<string name="main_app_version_go_update">Update now</string>
<!-- ja -->
<string name="main_app_version_go_update">アップデートする</string>
```

### main_app_version_latest_label

```xml
<!-- ko -->
<string name="main_app_version_latest_label">최신 버전</string>
<!-- en -->
<string name="main_app_version_latest_label">Latest version</string>
<!-- ja -->
<string name="main_app_version_latest_label">最新バージョン</string>
```

### main_app_version_recheck

```xml
<!-- ko -->
<string name="main_app_version_recheck">버전 다시 확인</string>
<!-- en -->
<string name="main_app_version_recheck">Check version again</string>
<!-- ja -->
<string name="main_app_version_recheck">バージョンを再確認</string>
```

### main_app_version_status_latest

```xml
<!-- ko -->
<string name="main_app_version_status_latest">최신 버전이에요.</string>
<!-- en -->
<string name="main_app_version_status_latest">You\'re on the latest version.</string>
<!-- ja -->
<string name="main_app_version_status_latest">最新バージョンです。</string>
```

### main_app_version_status_loading

```xml
<!-- ko -->
<string name="main_app_version_status_loading">업데이트 확인 중…</string>
<!-- en -->
<string name="main_app_version_status_loading">Checking for updates…</string>
<!-- ja -->
<string name="main_app_version_status_loading">アップデートを確認中…</string>
```

### main_app_version_status_unavailable

```xml
<!-- ko -->
<string name="main_app_version_status_unavailable">버전을 확인하지 못했어요.</string>
<!-- en -->
<string name="main_app_version_status_unavailable">Couldn\'t check the version.</string>
<!-- ja -->
<string name="main_app_version_status_unavailable">バージョンを確認できませんでした。</string>
```

### main_app_version_status_update_available

```xml
<!-- ko -->
<string name="main_app_version_status_update_available">새 버전이 있어요.</string>
<!-- en -->
<string name="main_app_version_status_update_available">A new version is available.</string>
<!-- ja -->
<string name="main_app_version_status_update_available">新しいバージョンがあります。</string>
```

### main_app_version_title

```xml
<!-- ko -->
<string name="main_app_version_title">앱 버전</string>
<!-- en -->
<string name="main_app_version_title">App version</string>
<!-- ja -->
<string name="main_app_version_title">アプリバージョン</string>
```

### main_app_version_unknown

```xml
<!-- ko -->
<string name="main_app_version_unknown">버전 정보를 확인할 수 없어요</string>
<!-- en -->
<string name="main_app_version_unknown">Version info unavailable</string>
<!-- ja -->
<string name="main_app_version_unknown">バージョン情報を確認できません</string>
```

### main_back_top_bar_back

```xml
<!-- ko -->
<string name="main_back_top_bar_back">뒤로 가기</string>
<!-- en -->
<string name="main_back_top_bar_back">Back</string>
<!-- ja -->
<string name="main_back_top_bar_back">戻る</string>
```

### main_chat_home_enter

```xml
<!-- ko -->
<string name="main_chat_home_enter">입장</string>
<!-- en -->
<string name="main_chat_home_enter">Join</string>
<!-- ja -->
<string name="main_chat_home_enter">参加</string>
```

### main_chat_home_tab_neighborhood

```xml
<!-- ko -->
<string name="main_chat_home_tab_neighborhood">%1$s 채팅방</string>
<!-- en -->
<string name="main_chat_home_tab_neighborhood">%1$s chats</string>
<!-- ja -->
<string name="main_chat_home_tab_neighborhood">%1$sのチャット</string>
```

### main_chat_home_tab_participating

```xml
<!-- ko -->
<string name="main_chat_home_tab_participating">참여 중</string>
<!-- en -->
<string name="main_chat_home_tab_participating">Joined</string>
<!-- ja -->
<string name="main_chat_home_tab_participating">参加中</string>
```

### main_chat_home_title

```xml
<!-- ko -->
<string name="main_chat_home_title">채팅</string>
<!-- en -->
<string name="main_chat_home_title">Chats</string>
<!-- ja -->
<string name="main_chat_home_title">チャット</string>
```

### main_chat_region_rooms

```xml
<!-- ko -->
<string name="main_chat_region_rooms">%1$s 채팅방</string>
<!-- en -->
<string name="main_chat_region_rooms">%1$s chat rooms</string>
<!-- ja -->
<string name="main_chat_region_rooms">%1$sのチャットルーム</string>
```

### main_chat_room_back

```xml
<!-- ko -->
<string name="main_chat_room_back">뒤로 가기</string>
<!-- en -->
<string name="main_chat_room_back">Back</string>
<!-- ja -->
<string name="main_chat_room_back">戻る</string>
```

### main_chat_room_input_placeholder

```xml
<!-- ko -->
<string name="main_chat_room_input_placeholder">메시지 보내기</string>
<!-- en -->
<string name="main_chat_room_input_placeholder">Type a message</string>
<!-- ja -->
<string name="main_chat_room_input_placeholder">メッセージを入力</string>
```

### main_chat_room_send

```xml
<!-- ko -->
<string name="main_chat_room_send">전송</string>
<!-- en -->
<string name="main_chat_room_send">Send</string>
<!-- ja -->
<string name="main_chat_room_send">送信</string>
```

### main_copy_course_description

```xml
<!-- ko -->
<string name="main_copy_course_description">계획은 나만 볼 수 있고, 방문할 장소와 메모를 담을 수 있어요.</string>
<!-- en -->
<string name="main_copy_course_description">Plans are private. Add places to visit and notes.</string>
<!-- ja -->
<string name="main_copy_course_description">プランは自分だけに表示されます。訪問する場所やメモを追加できます。</string>
```

### main_copy_course_plan

```xml
<!-- ko -->
<string name="main_copy_course_plan">계획하기</string>
<!-- en -->
<string name="main_copy_course_plan">Make a plan</string>
<!-- ja -->
<string name="main_copy_course_plan">プランを作る</string>
```

### main_copy_course_review

```xml
<!-- ko -->
<string name="main_copy_course_review">후기 작성</string>
<!-- en -->
<string name="main_copy_course_review">Write a review</string>
<!-- ja -->
<string name="main_copy_course_review">レビューを書く</string>
```

### main_copy_course_title

```xml
<!-- ko -->
<string name="main_copy_course_title">어떻게 복제할까요?</string>
<!-- en -->
<string name="main_copy_course_title">How would you like to copy this course?</string>
<!-- ja -->
<string name="main_copy_course_title">どのようにコピーしますか？</string>
```

### main_copy_link_failed

```xml
<!-- ko -->
<string name="main_copy_link_failed">링크를 복사하지 못했어요</string>
<!-- en -->
<string name="main_copy_link_failed">Unable to copy the link</string>
<!-- ja -->
<string name="main_copy_link_failed">リンクをコピーできませんでした</string>
```

### main_copy_link_label

```xml
<!-- ko -->
<string name="main_copy_link_label">Courmy 링크</string>
<!-- en -->
<string name="main_copy_link_label">Courmy link</string>
<!-- ja -->
<string name="main_copy_link_label">Courmyリンク</string>
```

### main_copy_link_success

```xml
<!-- ko -->
<string name="main_copy_link_success">링크를 복사했어요</string>
<!-- en -->
<string name="main_copy_link_success">Link copied</string>
<!-- ja -->
<string name="main_copy_link_success">リンクをコピーしました</string>
```

### main_course_like_count_description

```xml
<!-- ko -->
<string name="main_course_like_count_description">좋아요 %1$s</string>
<!-- en -->
<string name="main_course_like_count_description">%1$s likes</string>
<!-- ja -->
<string name="main_course_like_count_description">いいね %1$s</string>
```

### main_follow_button_follow

```xml
<!-- ko -->
<string name="main_follow_button_follow">팔로우</string>
<!-- en -->
<string name="main_follow_button_follow">Follow</string>
<!-- ja -->
<string name="main_follow_button_follow">フォロー</string>
```

### main_follow_button_follow_back

```xml
<!-- ko -->
<string name="main_follow_button_follow_back">맞팔로우</string>
<!-- en -->
<string name="main_follow_button_follow_back">Follow back</string>
<!-- ja -->
<string name="main_follow_button_follow_back">フォローバック</string>
```

### main_follow_button_following

```xml
<!-- ko -->
<string name="main_follow_button_following">팔로잉</string>
<!-- en -->
<string name="main_follow_button_following">Following</string>
<!-- ja -->
<string name="main_follow_button_following">フォロー中</string>
```

### main_follow_button_mutual_following

```xml
<!-- ko -->
<string name="main_follow_button_mutual_following">서로 팔로잉</string>
<!-- en -->
<string name="main_follow_button_mutual_following">Mutual</string>
<!-- ja -->
<string name="main_follow_button_mutual_following">相互フォロー</string>
```

### main_follow_list_back

```xml
<!-- ko -->
<string name="main_follow_list_back">뒤로가기</string>
<!-- en -->
<string name="main_follow_list_back">Back</string>
<!-- ja -->
<string name="main_follow_list_back">戻る</string>
```

### main_follow_list_empty_follower

```xml
<!-- ko -->
<string name="main_follow_list_empty_follower">아직 팔로워가 없어요.</string>
<!-- en -->
<string name="main_follow_list_empty_follower">No followers yet.</string>
<!-- ja -->
<string name="main_follow_list_empty_follower">まだフォロワーがいません。</string>
```

### main_follow_list_empty_following

```xml
<!-- ko -->
<string name="main_follow_list_empty_following">아직 팔로우한 사람이 없어요.</string>
<!-- en -->
<string name="main_follow_list_empty_following">Not following anyone yet.</string>
<!-- ja -->
<string name="main_follow_list_empty_following">まだ誰もフォローしていません。</string>
```

### main_follow_list_remove

```xml
<!-- ko -->
<string name="main_follow_list_remove">%1$s 제거</string>
<!-- en -->
<string name="main_follow_list_remove">Remove %1$s</string>
<!-- ja -->
<string name="main_follow_list_remove">%1$sを削除</string>
```

### main_follow_list_retry

```xml
<!-- ko -->
<string name="main_follow_list_retry">다시 시도</string>
<!-- en -->
<string name="main_follow_list_retry">Try again</string>
<!-- ja -->
<string name="main_follow_list_retry">再試行</string>
```

### main_follow_list_tab_follower

```xml
<!-- ko -->
<string name="main_follow_list_tab_follower">팔로워</string>
<!-- en -->
<string name="main_follow_list_tab_follower">Followers</string>
<!-- ja -->
<string name="main_follow_list_tab_follower">フォロワー</string>
```

### main_follow_list_tab_following

```xml
<!-- ko -->
<string name="main_follow_list_tab_following">팔로잉</string>
<!-- en -->
<string name="main_follow_list_tab_following">Following</string>
<!-- ja -->
<string name="main_follow_list_tab_following">フォロー中</string>
```

### main_follow_list_title

```xml
<!-- ko -->
<string name="main_follow_list_title">팔로우 목록</string>
<!-- en -->
<string name="main_follow_list_title">Follows</string>
<!-- ja -->
<string name="main_follow_list_title">フォロー一覧</string>
```

### main_follow_list_unfollow_dialog_description

```xml
<!-- ko -->
<string name="main_follow_list_unfollow_dialog_description">%1$s 님 팔로잉을 해제해요.</string>
<!-- en -->
<string name="main_follow_list_unfollow_dialog_description">You\'ll unfollow %1$s.</string>
<!-- ja -->
<string name="main_follow_list_unfollow_dialog_description">%1$sさんのフォローを解除します。</string>
```

### main_follow_list_unfollow_dialog_title

```xml
<!-- ko -->
<string name="main_follow_list_unfollow_dialog_title">팔로잉을 해제할까요?</string>
<!-- en -->
<string name="main_follow_list_unfollow_dialog_title">Unfollow?</string>
<!-- ja -->
<string name="main_follow_list_unfollow_dialog_title">フォローを解除しますか？</string>
```

### main_guest_my_benefit_click_label

```xml
<!-- ko -->
<string name="main_guest_my_benefit_click_label">로그인 화면으로 이동</string>
<!-- en -->
<string name="main_guest_my_benefit_click_label">Go to login screen</string>
<!-- ja -->
<string name="main_guest_my_benefit_click_label">ログイン画面へ移動</string>
```

### main_guest_my_benefit_follow_subtitle

```xml
<!-- ko -->
<string name="main_guest_my_benefit_follow_subtitle">취향이 맞는 사람의 코스를 만나보세요</string>
<!-- en -->
<string name="main_guest_my_benefit_follow_subtitle">Discover courses from people who share your taste</string>
<!-- ja -->
<string name="main_guest_my_benefit_follow_subtitle">好みの合う人のコースに出会いましょう</string>
```

### main_guest_my_benefit_follow_title

```xml
<!-- ko -->
<string name="main_guest_my_benefit_follow_title">친구 팔로우</string>
<!-- en -->
<string name="main_guest_my_benefit_follow_title">Follow friends</string>
<!-- ja -->
<string name="main_guest_my_benefit_follow_title">友達をフォロー</string>
```

### main_guest_my_benefit_plan_subtitle

```xml
<!-- ko -->
<string name="main_guest_my_benefit_plan_subtitle">원하는 장소로 나만의 일정을 짜보세요</string>
<!-- en -->
<string name="main_guest_my_benefit_plan_subtitle">Build your own itinerary from the places you want</string>
<!-- ja -->
<string name="main_guest_my_benefit_plan_subtitle">好きなスポットで自分だけの日程を作りましょう</string>
```

### main_guest_my_benefit_plan_title

```xml
<!-- ko -->
<string name="main_guest_my_benefit_plan_title">여행 계획</string>
<!-- en -->
<string name="main_guest_my_benefit_plan_title">Trip planning</string>
<!-- ja -->
<string name="main_guest_my_benefit_plan_title">旅行プラン</string>
```

### main_guest_my_benefit_review_subtitle

```xml
<!-- ko -->
<string name="main_guest_my_benefit_review_subtitle">다녀온 장소의 경험을 나눠보세요</string>
<!-- en -->
<string name="main_guest_my_benefit_review_subtitle">Share your experience of places you\'ve been</string>
<!-- ja -->
<string name="main_guest_my_benefit_review_subtitle">訪れたスポットの体験をシェアしましょう</string>
```

### main_guest_my_benefit_review_title

```xml
<!-- ko -->
<string name="main_guest_my_benefit_review_title">방문 후기</string>
<!-- en -->
<string name="main_guest_my_benefit_review_title">Reviews</string>
<!-- ja -->
<string name="main_guest_my_benefit_review_title">訪問レビュー</string>
```

### main_guest_my_benefit_saved_subtitle

```xml
<!-- ko -->
<string name="main_guest_my_benefit_saved_subtitle">가고 싶은 장소와 코스를 모아보세요</string>
<!-- en -->
<string name="main_guest_my_benefit_saved_subtitle">Collect places and courses you want to visit</string>
<!-- ja -->
<string name="main_guest_my_benefit_saved_subtitle">行きたいスポットやコースを集めましょう</string>
```

### main_guest_my_benefit_saved_title

```xml
<!-- ko -->
<string name="main_guest_my_benefit_saved_title">장소·코스 저장</string>
<!-- en -->
<string name="main_guest_my_benefit_saved_title">Save places &amp; courses</string>
<!-- ja -->
<string name="main_guest_my_benefit_saved_title">スポット・コース保存</string>
```

### main_guest_my_benefits_header

```xml
<!-- ko -->
<string name="main_guest_my_benefits_header">가입하면 할 수 있어요</string>
<!-- en -->
<string name="main_guest_my_benefits_header">Sign up to unlock these</string>
<!-- ja -->
<string name="main_guest_my_benefits_header">登録するとできること</string>
```

### main_guest_my_login_button

```xml
<!-- ko -->
<string name="main_guest_my_login_button">로그인 / 회원가입</string>
<!-- en -->
<string name="main_guest_my_login_button">Log in / Sign up</string>
<!-- ja -->
<string name="main_guest_my_login_button">ログイン / 新規登録</string>
```

### main_guest_my_settings_cd

```xml
<!-- ko -->
<string name="main_guest_my_settings_cd">설정</string>
<!-- en -->
<string name="main_guest_my_settings_cd">Settings</string>
<!-- ja -->
<string name="main_guest_my_settings_cd">設定</string>
```

### main_guest_my_title

```xml
<!-- ko -->
<string name="main_guest_my_title">마이</string>
<!-- en -->
<string name="main_guest_my_title">My</string>
<!-- ja -->
<string name="main_guest_my_title">マイ</string>
```

### main_guest_my_welcome_subtitle

```xml
<!-- ko -->
<string name="main_guest_my_welcome_subtitle">로그인하고 취향 코스를 받아보세요</string>
<!-- en -->
<string name="main_guest_my_welcome_subtitle">Log in to get courses made for you</string>
<!-- ja -->
<string name="main_guest_my_welcome_subtitle">ログインして好みのコースを受け取りましょう</string>
```

### main_guest_my_welcome_title

```xml
<!-- ko -->
<string name="main_guest_my_welcome_title">게스트님, 반가워요</string>
<!-- en -->
<string name="main_guest_my_welcome_title">Welcome, guest</string>
<!-- ja -->
<string name="main_guest_my_welcome_title">ゲストさん、ようこそ</string>
```

### main_home_course_card_bookmark_add

```xml
<!-- ko -->
<string name="main_home_course_card_bookmark_add">저장</string>
<!-- en -->
<string name="main_home_course_card_bookmark_add">Save</string>
<!-- ja -->
<string name="main_home_course_card_bookmark_add">保存</string>
```

### main_home_course_card_bookmark_remove

```xml
<!-- ko -->
<string name="main_home_course_card_bookmark_remove">저장 취소</string>
<!-- en -->
<string name="main_home_course_card_bookmark_remove">Remove bookmark</string>
<!-- ja -->
<string name="main_home_course_card_bookmark_remove">保存を解除</string>
```

### main_home_greeting_guest

```xml
<!-- ko -->
<string name="main_home_greeting_guest">어디로 떠나볼까요?</string>
<!-- en -->
<string name="main_home_greeting_guest">Where to today?</string>
<!-- ja -->
<string name="main_home_greeting_guest">今日はどこへ行きましょう?</string>
```

### main_home_greeting_user

```xml
<!-- ko -->
<string name="main_home_greeting_user">%1$s님, 오늘은\n어디로 떠나볼까요?</string>
<!-- en -->
<string name="main_home_greeting_user">%1$s,\nwhere to today?</string>
<!-- ja -->
<string name="main_home_greeting_user">%1$sさん、今日は\nどこへ行きましょう?</string>
```

### main_home_in_progress_dismiss

```xml
<!-- ko -->
<string name="main_home_in_progress_dismiss">진행 중인 코스 지우기</string>
<!-- en -->
<string name="main_home_in_progress_dismiss">Dismiss course in progress</string>
<!-- ja -->
<string name="main_home_in_progress_dismiss">進行中のコースを消す</string>
```

### main_home_in_progress_end_description

```xml
<!-- ko -->
<string name="main_home_in_progress_end_description">종료된 코스는 완주되어 저장함에서 볼 수 있어요.</string>
<!-- en -->
<string name="main_home_in_progress_end_description">Ended courses are completed and saved to your library.</string>
<!-- ja -->
<string name="main_home_in_progress_end_description">終了したコースは完走として保存箱で見られます。</string>
```

### main_home_in_progress_end_title

```xml
<!-- ko -->
<string name="main_home_in_progress_end_title">진행 중인 코스를 종료할까요?</string>
<!-- en -->
<string name="main_home_in_progress_end_title">End this course?</string>
<!-- ja -->
<string name="main_home_in_progress_end_title">進行中のコースを終了しますか?</string>
```

### main_home_in_progress_label

```xml
<!-- ko -->
<string name="main_home_in_progress_label">진행 중인 코스</string>
<!-- en -->
<string name="main_home_in_progress_label">In progress</string>
<!-- ja -->
<string name="main_home_in_progress_label">進行中のコース</string>
```

### main_home_in_progress_private_label

```xml
<!-- ko -->
<string name="main_home_in_progress_private_label">비공개된 코스</string>
<!-- en -->
<string name="main_home_in_progress_private_label">Private course</string>
<!-- ja -->
<string name="main_home_in_progress_private_label">非公開のコース</string>
```

### main_home_nearby_allow_location

```xml
<!-- ko -->
<string name="main_home_nearby_allow_location">위치 허용</string>
<!-- en -->
<string name="main_home_nearby_allow_location">Allow location</string>
<!-- ja -->
<string name="main_home_nearby_allow_location">位置情報を許可</string>
```

### main_home_nearby_empty_message

```xml
<!-- ko -->
<string name="main_home_nearby_empty_message">근처에 저장한 장소가 없어요</string>
<!-- en -->
<string name="main_home_nearby_empty_message">No saved places nearby</string>
<!-- ja -->
<string name="main_home_nearby_empty_message">近くに保存したスポットがありません</string>
```

### main_home_nearby_enable_location

```xml
<!-- ko -->
<string name="main_home_nearby_enable_location">위치 켜기</string>
<!-- en -->
<string name="main_home_nearby_enable_location">Turn on location</string>
<!-- ja -->
<string name="main_home_nearby_enable_location">位置情報をオン</string>
```

### main_home_nearby_find_on_map

```xml
<!-- ko -->
<string name="main_home_nearby_find_on_map">지도에서 장소 찾기</string>
<!-- en -->
<string name="main_home_nearby_find_on_map">Find places on the map</string>
<!-- ja -->
<string name="main_home_nearby_find_on_map">地図でスポットを探す</string>
```

### main_home_nearby_gps_message

```xml
<!-- ko -->
<string name="main_home_nearby_gps_message">기기 위치(GPS)가 꺼져 있어 근처 장소를 찾을 수 없어요</string>
<!-- en -->
<string name="main_home_nearby_gps_message">Location (GPS) is off, so we can\'t find nearby places</string>
<!-- ja -->
<string name="main_home_nearby_gps_message">位置情報(GPS)がオフのため、近くのスポットを探せません</string>
```

### main_home_nearby_permission_message

```xml
<!-- ko -->
<string name="main_home_nearby_permission_message">위치를 허용하면 근처에 저장한 장소가 여기에 보여요</string>
<!-- en -->
<string name="main_home_nearby_permission_message">Allow location to see places you\'ve saved nearby</string>
<!-- ja -->
<string name="main_home_nearby_permission_message">位置情報を許可すると、近くの保存スポットがここに表示されます</string>
```

### main_home_nearby_see_on_map

```xml
<!-- ko -->
<string name="main_home_nearby_see_on_map">지도에서 보기</string>
<!-- en -->
<string name="main_home_nearby_see_on_map">View on map</string>
<!-- ja -->
<string name="main_home_nearby_see_on_map">地図で見る</string>
```

### main_home_nearby_title

```xml
<!-- ko -->
<string name="main_home_nearby_title">근처 저장 장소</string>
<!-- en -->
<string name="main_home_nearby_title">Saved nearby</string>
<!-- ja -->
<string name="main_home_nearby_title">近くの保存スポット</string>
```

### main_home_place_not_visited

```xml
<!-- ko -->
<string name="main_home_place_not_visited">미방문</string>
<!-- en -->
<string name="main_home_place_not_visited">Not visited</string>
<!-- ja -->
<string name="main_home_place_not_visited">未訪問</string>
```

### main_home_place_visited

```xml
<!-- ko -->
<string name="main_home_place_visited">방문</string>
<!-- en -->
<string name="main_home_place_visited">Visited</string>
<!-- ja -->
<string name="main_home_place_visited">訪問済み</string>
```

### main_home_profile_click_label

```xml
<!-- ko -->
<string name="main_home_profile_click_label">마이로 이동</string>
<!-- en -->
<string name="main_home_profile_click_label">Go to My page</string>
<!-- ja -->
<string name="main_home_profile_click_label">マイページへ移動</string>
```

### main_home_profile_description

```xml
<!-- ko -->
<string name="main_home_profile_description">내 프로필</string>
<!-- en -->
<string name="main_home_profile_description">My profile</string>
<!-- ja -->
<string name="main_home_profile_description">マイプロフィール</string>
```

### main_home_region_browse_title

```xml
<!-- ko -->
<string name="main_home_region_browse_title">지역별 둘러보기</string>
<!-- en -->
<string name="main_home_region_browse_title">Browse by region</string>
<!-- ja -->
<string name="main_home_region_browse_title">エリアで探す</string>
```

### main_home_save_label

```xml
<!-- ko -->
<string name="main_home_save_label">저장</string>
<!-- en -->
<string name="main_home_save_label">Saves</string>
<!-- ja -->
<string name="main_home_save_label">保存</string>
```

### main_home_see_all

```xml
<!-- ko -->
<string name="main_home_see_all">전체보기</string>
<!-- en -->
<string name="main_home_see_all">See all</string>
<!-- ja -->
<string name="main_home_see_all">すべて見る</string>
```

### main_home_theme_browse_title

```xml
<!-- ko -->
<string name="main_home_theme_browse_title">테마별 둘러보기</string>
<!-- en -->
<string name="main_home_theme_browse_title">Browse by theme</string>
<!-- ja -->
<string name="main_home_theme_browse_title">テーマで探す</string>
```

### main_interest_load_feedback_error

```xml
<!-- ko -->
<string name="main_interest_load_feedback_error">잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_interest_load_feedback_error">Please try again in a moment.</string>
<!-- ja -->
<string name="main_interest_load_feedback_error">しばらくしてからもう一度お試しください。</string>
```

### main_interest_load_feedback_loading

```xml
<!-- ko -->
<string name="main_interest_load_feedback_loading">저장된 선택을 확인하고 있어요</string>
<!-- en -->
<string name="main_interest_load_feedback_loading">Checking your saved selections</string>
<!-- ja -->
<string name="main_interest_load_feedback_loading">保存した選択を確認しています</string>
```

### main_interest_load_feedback_retry

```xml
<!-- ko -->
<string name="main_interest_load_feedback_retry">새로고침</string>
<!-- en -->
<string name="main_interest_load_feedback_retry">Refresh</string>
<!-- ja -->
<string name="main_interest_load_feedback_retry">更新</string>
```

### main_interest_region_chip_deselect_a11y

```xml
<!-- ko -->
<string name="main_interest_region_chip_deselect_a11y">%1$s 선택 해제</string>
<!-- en -->
<string name="main_interest_region_chip_deselect_a11y">Remove %1$s</string>
<!-- ja -->
<string name="main_interest_region_chip_deselect_a11y">%1$s の選択を解除</string>
```

### main_interest_region_chip_remove

```xml
<!-- ko -->
<string name="main_interest_region_chip_remove">%1$s ×</string>
<!-- en -->
<string name="main_interest_region_chip_remove">%1$s ×</string>
<!-- ja -->
<string name="main_interest_region_chip_remove">%1$s ×</string>
```

### main_interest_region_cta_empty

```xml
<!-- ko -->
<string name="main_interest_region_cta_empty">지역을 1개 이상 선택해 주세요</string>
<!-- en -->
<string name="main_interest_region_cta_empty">Please select at least one area</string>
<!-- ja -->
<string name="main_interest_region_cta_empty">エリアを1つ以上選択してください</string>
```

### main_interest_region_cta_next

```xml
<!-- ko -->
<string name="main_interest_region_cta_next">다음</string>
<!-- en -->
<string name="main_interest_region_cta_next">Next</string>
<!-- ja -->
<string name="main_interest_region_cta_next">次へ</string>
```

### main_interest_region_cta_save

```xml
<!-- ko -->
<string name="main_interest_region_cta_save">저장</string>
<!-- en -->
<string name="main_interest_region_cta_save">Save</string>
<!-- ja -->
<string name="main_interest_region_cta_save">保存</string>
```

### main_interest_region_heading

```xml
<!-- ko -->
<string name="main_interest_region_heading">자주 가는 지역을 골라주세요</string>
<!-- en -->
<string name="main_interest_region_heading">Pick the areas you visit often</string>
<!-- ja -->
<string name="main_interest_region_heading">よく行くエリアを選んでください</string>
```

### main_interest_region_save_error

```xml
<!-- ko -->
<string name="main_interest_region_save_error">저장하지 못했어요. 선택한 지역을 유지했으니 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_interest_region_save_error">Couldn\'t save. Your selected areas were kept, so please try again.</string>
<!-- ja -->
<string name="main_interest_region_save_error">保存できませんでした。選択したエリアは保持したので、もう一度お試しください。</string>
```

### main_interest_region_selected_count

```xml
<!-- ko -->
<string name="main_interest_region_selected_count">선택 %1$d ·</string>
<!-- en -->
<string name="main_interest_region_selected_count">%1$d selected ·</string>
<!-- ja -->
<string name="main_interest_region_selected_count">%1$d件選択 ·</string>
```

### main_interest_region_sido_all

```xml
<!-- ko -->
<string name="main_interest_region_sido_all">%1$s 전체</string>
<!-- en -->
<string name="main_interest_region_sido_all">All of %1$s</string>
<!-- ja -->
<string name="main_interest_region_sido_all">%1$s全体</string>
```

### main_interest_region_sido_all_desc

```xml
<!-- ko -->
<string name="main_interest_region_sido_all_desc">%1$s 전체 선택, 하위 지역 모두 포함</string>
<!-- en -->
<string name="main_interest_region_sido_all_desc">Select all of %1$s, including every sub-area</string>
<!-- ja -->
<string name="main_interest_region_sido_all_desc">%1$s全体を選択、下位エリアもすべて含む</string>
```

### main_interest_region_subheading

```xml
<!-- ko -->
<string name="main_interest_region_subheading">최소 1개 · 시·도 전체 또는 시·군·구 선택</string>
<!-- en -->
<string name="main_interest_region_subheading">At least 1 · Whole province or city/district</string>
<!-- ja -->
<string name="main_interest_region_subheading">1つ以上 · 都道府県全体または市区町村を選択</string>
```

### main_interest_region_title

```xml
<!-- ko -->
<string name="main_interest_region_title">관심 지역</string>
<!-- en -->
<string name="main_interest_region_title">Favorite Areas</string>
<!-- ja -->
<string name="main_interest_region_title">関心エリア</string>
```

### main_interest_region_view_all

```xml
<!-- ko -->
<string name="main_interest_region_view_all">‹ 전국 보기</string>
<!-- en -->
<string name="main_interest_region_view_all">‹ View all</string>
<!-- ja -->
<string name="main_interest_region_view_all">‹ 全国を表示</string>
```

### main_interest_region_zoom_desc

```xml
<!-- ko -->
<string name="main_interest_region_zoom_desc">%1$s 확대</string>
<!-- en -->
<string name="main_interest_region_zoom_desc">Zoom into %1$s</string>
<!-- ja -->
<string name="main_interest_region_zoom_desc">%1$s を拡大</string>
```

### main_interest_theme_headline

```xml
<!-- ko -->
<string name="main_interest_theme_headline">어떤 곳을\n좋아하세요?</string>
<!-- en -->
<string name="main_interest_theme_headline">What kind of places\ndo you like?</string>
<!-- ja -->
<string name="main_interest_theme_headline">どんな場所が\nお好きですか？</string>
```

### main_interest_theme_min_count

```xml
<!-- ko -->
<string name="main_interest_theme_min_count">최소 %1$d개 이상 · </string>
<!-- en -->
<string name="main_interest_theme_min_count">At least %1$d · </string>
<!-- ja -->
<string name="main_interest_theme_min_count">%1$d個以上 · </string>
```

### main_interest_theme_next

```xml
<!-- ko -->
<string name="main_interest_theme_next">다음</string>
<!-- en -->
<string name="main_interest_theme_next">Next</string>
<!-- ja -->
<string name="main_interest_theme_next">次へ</string>
```

### main_interest_theme_save

```xml
<!-- ko -->
<string name="main_interest_theme_save">저장</string>
<!-- en -->
<string name="main_interest_theme_save">Save</string>
<!-- ja -->
<string name="main_interest_theme_save">保存</string>
```

### main_interest_theme_save_error

```xml
<!-- ko -->
<string name="main_interest_theme_save_error">저장하지 못했어요. 선택한 테마를 유지했으니 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_interest_theme_save_error">Couldn\'t save. We kept your picks, so please try again.</string>
<!-- ja -->
<string name="main_interest_theme_save_error">保存できませんでした。選んだテーマは残してあるので、もう一度お試しください。</string>
```

### main_interest_theme_select_min

```xml
<!-- ko -->
<string name="main_interest_theme_select_min">최소 %1$d개 이상 선택</string>
<!-- en -->
<string name="main_interest_theme_select_min">Pick at least %1$d</string>
<!-- ja -->
<string name="main_interest_theme_select_min">%1$d個以上選んでください</string>
```

### main_interest_theme_selected_count

```xml
<!-- ko -->
<string name="main_interest_theme_selected_count">%1$d개 선택됨</string>
<!-- en -->
<string name="main_interest_theme_selected_count">%1$d selected</string>
<!-- ja -->
<string name="main_interest_theme_selected_count">%1$d個選択中</string>
```

### main_interest_theme_title

```xml
<!-- ko -->
<string name="main_interest_theme_title">관심 테마</string>
<!-- en -->
<string name="main_interest_theme_title">Interests</string>
<!-- ja -->
<string name="main_interest_theme_title">興味のあるテーマ</string>
```

### main_login_back_to_guest

```xml
<!-- ko -->
<string name="main_login_back_to_guest">게스트로 돌아가기</string>
<!-- en -->
<string name="main_login_back_to_guest">Back to guest</string>
<!-- ja -->
<string name="main_login_back_to_guest">ゲストに戻る</string>
```

### main_login_google_account_guide

```xml
<!-- ko -->
<string name="main_login_google_account_guide">Google 계정을 선택해 주세요.\n선택창이 열리지 않으면 취소 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_login_google_account_guide">Please choose a Google account.\nIf the picker doesn\'t open, cancel and try again.</string>
<!-- ja -->
<string name="main_login_google_account_guide">Googleアカウントを選択してください。\n選択画面が開かない場合は、キャンセルしてもう一度お試しください。</string>
```

### main_login_google_cancel

```xml
<!-- ko -->
<string name="main_login_google_cancel">Google 로그인 취소</string>
<!-- en -->
<string name="main_login_google_cancel">Cancel Google sign-in</string>
<!-- ja -->
<string name="main_login_google_cancel">Googleログインをキャンセル</string>
```

### main_login_google_start

```xml
<!-- ko -->
<string name="main_login_google_start">Google로 시작하기</string>
<!-- en -->
<string name="main_login_google_start">Continue with Google</string>
<!-- ja -->
<string name="main_login_google_start">Googleで始める</string>
```

### main_login_kakao_start

```xml
<!-- ko -->
<string name="main_login_kakao_start">카카오로 시작하기</string>
<!-- en -->
<string name="main_login_kakao_start">Continue with Kakao</string>
<!-- ja -->
<string name="main_login_kakao_start">Kakaoで始める</string>
```

### main_login_naver_start

```xml
<!-- ko -->
<string name="main_login_naver_start">네이버로 시작하기</string>
<!-- en -->
<string name="main_login_naver_start">Continue with Naver</string>
<!-- ja -->
<string name="main_login_naver_start">Naverで始める</string>
```

### main_login_profile_hint

```xml
<!-- ko -->
<string name="main_login_profile_hint">처음이라면 프로필 설정이 이어져요.</string>
<!-- en -->
<string name="main_login_profile_hint">New here? Profile setup comes next.</string>
<!-- ja -->
<string name="main_login_profile_hint">初めての場合はプロフィール設定に進みます。</string>
```

### main_login_profile_setup_hint

```xml
<!-- ko -->
<string name="main_login_profile_setup_hint">처음이라면 프로필 설정이 이어져요.</string>
<!-- en -->
<string name="main_login_profile_setup_hint">New here? Profile setup comes next.</string>
<!-- ja -->
<string name="main_login_profile_setup_hint">初めてならプロフィール設定に進みます。</string>
```

### main_login_start_google

```xml
<!-- ko -->
<string name="main_login_start_google">Google로 시작하기</string>
<!-- en -->
<string name="main_login_start_google">Continue with Google</string>
<!-- ja -->
<string name="main_login_start_google">Googleで始める</string>
```

### main_login_start_kakao

```xml
<!-- ko -->
<string name="main_login_start_kakao">카카오로 시작하기</string>
<!-- en -->
<string name="main_login_start_kakao">Continue with Kakao</string>
<!-- ja -->
<string name="main_login_start_kakao">Kakaoで始める</string>
```

### main_login_start_naver

```xml
<!-- ko -->
<string name="main_login_start_naver">네이버로 시작하기</string>
<!-- en -->
<string name="main_login_start_naver">Continue with Naver</string>
<!-- ja -->
<string name="main_login_start_naver">Naverで始める</string>
```

### main_login_subtitle

```xml
<!-- ko -->
<string name="main_login_subtitle">계정으로 3초 만에 자동 연결돼요.\n처음이면 바로 가입돼요.</string>
<!-- en -->
<string name="main_login_subtitle">Connect in 3 seconds with your account.\nNew here? You\'re signed up instantly.</string>
<!-- ja -->
<string name="main_login_subtitle">アカウントで3秒ですぐに連携。\n初めてならそのまま登録されます。</string>
```

### main_login_title

```xml
<!-- ko -->
<string name="main_login_title">Courmy와\n함께 시작해요</string>
<!-- en -->
<string name="main_login_title">Let\'s get started\nwith Courmy</string>
<!-- ja -->
<string name="main_login_title">Courmyと\n一緒に始めましょう</string>
```

### main_map_empty_loading

```xml
<!-- ko -->
<string name="main_map_empty_loading">주변 장소를 찾는 중이에요</string>
<!-- en -->
<string name="main_map_empty_loading">Finding places nearby</string>
<!-- ja -->
<string name="main_map_empty_loading">近くのスポットを探しています</string>
```

### main_map_empty_no_result

```xml
<!-- ko -->
<string name="main_map_empty_no_result">조건에 맞는 장소가 없어요</string>
<!-- en -->
<string name="main_map_empty_no_result">No places match</string>
<!-- ja -->
<string name="main_map_empty_no_result">条件に合うスポットがありません</string>
```

### main_map_my_location_description

```xml
<!-- ko -->
<string name="main_map_my_location_description">내 위치</string>
<!-- en -->
<string name="main_map_my_location_description">My location</string>
<!-- ja -->
<string name="main_map_my_location_description">現在地</string>
```

### main_map_place_closed

```xml
<!-- ko -->
<string name="main_map_place_closed">영업 종료</string>
<!-- en -->
<string name="main_map_place_closed">Closed</string>
<!-- ja -->
<string name="main_map_place_closed">営業終了</string>
```

### main_map_place_open_now

```xml
<!-- ko -->
<string name="main_map_place_open_now">영업 중</string>
<!-- en -->
<string name="main_map_place_open_now">Open</string>
<!-- ja -->
<string name="main_map_place_open_now">営業中</string>
```

### main_map_place_open_until

```xml
<!-- ko -->
<string name="main_map_place_open_until">영업 중 · %1$s 종료</string>
<!-- en -->
<string name="main_map_place_open_until">Open · Closes %1$s</string>
<!-- ja -->
<string name="main_map_place_open_until">営業中 · %1$s 閉店</string>
```

### main_map_place_save_toggle_add

```xml
<!-- ko -->
<string name="main_map_place_save_toggle_add">저장</string>
<!-- en -->
<string name="main_map_place_save_toggle_add">Save</string>
<!-- ja -->
<string name="main_map_place_save_toggle_add">保存</string>
```

### main_map_place_save_toggle_remove

```xml
<!-- ko -->
<string name="main_map_place_save_toggle_remove">저장 해제</string>
<!-- en -->
<string name="main_map_place_save_toggle_remove">Remove from saved</string>
<!-- ja -->
<string name="main_map_place_save_toggle_remove">保存を解除</string>
```

### main_map_search_bar_back

```xml
<!-- ko -->
<string name="main_map_search_bar_back">뒤로</string>
<!-- en -->
<string name="main_map_search_bar_back">Back</string>
<!-- ja -->
<string name="main_map_search_bar_back">戻る</string>
```

### main_map_search_bar_clear_query

```xml
<!-- ko -->
<string name="main_map_search_bar_clear_query">검색어 지우기</string>
<!-- en -->
<string name="main_map_search_bar_clear_query">Clear search</string>
<!-- ja -->
<string name="main_map_search_bar_clear_query">検索ワードを消す</string>
```

### main_map_search_result_count_label

```xml
<!-- ko -->
<string name="main_map_search_result_count_label">검색 결과</string>
<!-- en -->
<string name="main_map_search_result_count_label">Results</string>
<!-- ja -->
<string name="main_map_search_result_count_label">検索結果</string>
```

### main_map_search_result_empty

```xml
<!-- ko -->
<string name="main_map_search_result_empty">검색 결과가 없어요</string>
<!-- en -->
<string name="main_map_search_result_empty">No results found</string>
<!-- ja -->
<string name="main_map_search_result_empty">検索結果がありません</string>
```

### main_map_search_result_loading

```xml
<!-- ko -->
<string name="main_map_search_result_loading">검색 결과를 불러오는 중이에요</string>
<!-- en -->
<string name="main_map_search_result_loading">Loading results</string>
<!-- ja -->
<string name="main_map_search_result_loading">検索結果を読み込んでいます</string>
```

### main_map_search_result_my_location

```xml
<!-- ko -->
<string name="main_map_search_result_my_location">내 위치</string>
<!-- en -->
<string name="main_map_search_result_my_location">My location</string>
<!-- ja -->
<string name="main_map_search_result_my_location">現在地</string>
```

### main_map_search_result_zoom_in

```xml
<!-- ko -->
<string name="main_map_search_result_zoom_in">지도를 확대해 장소를 확인해 주세요</string>
<!-- en -->
<string name="main_map_search_result_zoom_in">Zoom in to see places</string>
<!-- ja -->
<string name="main_map_search_result_zoom_in">地図を拡大して場所を確認してください</string>
```

### main_map_sheet_collapse_list

```xml
<!-- ko -->
<string name="main_map_sheet_collapse_list">목록 접기</string>
<!-- en -->
<string name="main_map_sheet_collapse_list">Collapse list</string>
<!-- ja -->
<string name="main_map_sheet_collapse_list">リストを閉じる</string>
```

### main_map_sheet_expand_list

```xml
<!-- ko -->
<string name="main_map_sheet_expand_list">목록 펼치기</string>
<!-- en -->
<string name="main_map_sheet_expand_list">Expand list</string>
<!-- ja -->
<string name="main_map_sheet_expand_list">リストを開く</string>
```

### main_map_sort_distance

```xml
<!-- ko -->
<string name="main_map_sort_distance">거리순</string>
<!-- en -->
<string name="main_map_sort_distance">Distance</string>
<!-- ja -->
<string name="main_map_sort_distance">距離順</string>
```

### main_map_sort_rating

```xml
<!-- ko -->
<string name="main_map_sort_rating">평점순</string>
<!-- en -->
<string name="main_map_sort_rating">Rating</string>
<!-- ja -->
<string name="main_map_sort_rating">評価順</string>
```

### main_map_sort_saved

```xml
<!-- ko -->
<string name="main_map_sort_saved">저장순</string>
<!-- en -->
<string name="main_map_sort_saved">Saved</string>
<!-- ja -->
<string name="main_map_sort_saved">保存順</string>
```

### main_map_visible_place_count

```xml
<!-- ko -->
<string name="main_map_visible_place_count">보이는 장소 %1$d곳</string>
<!-- en -->
<string name="main_map_visible_place_count">%1$d places shown</string>
<!-- ja -->
<string name="main_map_visible_place_count">表示中のスポット%1$d件</string>
```

### main_my_create_course_subtitle

```xml
<!-- ko -->
<string name="main_my_create_course_subtitle">가 본 곳을 이어 첫 코스를 남겨 보세요</string>
<!-- en -->
<string name="main_my_create_course_subtitle">Link the places you\'ve been into your first course</string>
<!-- ja -->
<string name="main_my_create_course_subtitle">訪れた場所をつないで最初のコースを残しましょう</string>
```

### main_my_create_course_title

```xml
<!-- ko -->
<string name="main_my_create_course_title">나만의 코스 만들기</string>
<!-- en -->
<string name="main_my_create_course_title">Create your own course</string>
<!-- ja -->
<string name="main_my_create_course_title">自分だけのコースを作成</string>
```

### main_my_create_plan_subtitle

```xml
<!-- ko -->
<string name="main_my_create_plan_subtitle">가 보고 싶은 곳을 모아 계획을 세워 보세요</string>
<!-- en -->
<string name="main_my_create_plan_subtitle">Gather places you want to visit and make a plan</string>
<!-- ja -->
<string name="main_my_create_plan_subtitle">行きたい場所を集めてプランを立てましょう</string>
```

### main_my_create_plan_title

```xml
<!-- ko -->
<string name="main_my_create_plan_title">계획 생성하기</string>
<!-- en -->
<string name="main_my_create_plan_title">Create a plan</string>
<!-- ja -->
<string name="main_my_create_plan_title">プランを作成</string>
```

### main_my_settings_description

```xml
<!-- ko -->
<string name="main_my_settings_description">설정</string>
<!-- en -->
<string name="main_my_settings_description">Settings</string>
<!-- ja -->
<string name="main_my_settings_description">設定</string>
```

### main_my_share_description

```xml
<!-- ko -->
<string name="main_my_share_description">공유</string>
<!-- en -->
<string name="main_my_share_description">Share</string>
<!-- ja -->
<string name="main_my_share_description">共有</string>
```

### main_my_tab_plan

```xml
<!-- ko -->
<string name="main_my_tab_plan">계획</string>
<!-- en -->
<string name="main_my_tab_plan">Plans</string>
<!-- ja -->
<string name="main_my_tab_plan">プラン</string>
```

### main_notification_bell_description

```xml
<!-- ko -->
<string name="main_notification_bell_description">알림</string>
<!-- en -->
<string name="main_notification_bell_description">Notifications</string>
<!-- ja -->
<string name="main_notification_bell_description">通知</string>
```

### main_notification_bell_unread_description

```xml
<!-- ko -->
<string name="main_notification_bell_unread_description">알림, 안 읽은 알림 %1$d개</string>
<!-- en -->
<string name="main_notification_bell_unread_description">Notifications, %1$d unread</string>
<!-- ja -->
<string name="main_notification_bell_unread_description">通知、未読%1$d件</string>
```

### main_notification_consent_confirm

```xml
<!-- ko -->
<string name="main_notification_consent_confirm">동의하고 계속하기</string>
<!-- en -->
<string name="main_notification_consent_confirm">Agree and continue</string>
<!-- ja -->
<string name="main_notification_consent_confirm">同意して続ける</string>
```

### main_notification_consent_confirm_button

```xml
<!-- ko -->
<string name="main_notification_consent_confirm_button">동의하고 계속하기</string>
<!-- en -->
<string name="main_notification_consent_confirm_button">Agree and continue</string>
<!-- ja -->
<string name="main_notification_consent_confirm_button">同意して続ける</string>
```

### main_notification_consent_guide_confirm

```xml
<!-- ko -->
<string name="main_notification_consent_guide_confirm">기기 설정</string>
<!-- en -->
<string name="main_notification_consent_guide_confirm">Device settings</string>
<!-- ja -->
<string name="main_notification_consent_guide_confirm">端末設定</string>
```

### main_notification_consent_guide_description

```xml
<!-- ko -->
<string name="main_notification_consent_guide_description">활동 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요.</string>
<!-- en -->
<string name="main_notification_consent_guide_description">To get activity notifications, allow Courmy notifications in your device settings.</string>
<!-- ja -->
<string name="main_notification_consent_guide_description">アクティビティ通知を受け取るには、端末の設定でCourmyの通知を許可してください。</string>
```

### main_notification_consent_guide_dismiss

```xml
<!-- ko -->
<string name="main_notification_consent_guide_dismiss">닫기</string>
<!-- en -->
<string name="main_notification_consent_guide_dismiss">Close</string>
<!-- ja -->
<string name="main_notification_consent_guide_dismiss">閉じる</string>
```

### main_notification_consent_guide_title

```xml
<!-- ko -->
<string name="main_notification_consent_guide_title">기기 알림을 허용해 주세요</string>
<!-- en -->
<string name="main_notification_consent_guide_title">Allow device notifications</string>
<!-- ja -->
<string name="main_notification_consent_guide_title">端末の通知を許可してください</string>
```

### main_notification_consent_marketing_description

```xml
<!-- ko -->
<string name="main_notification_consent_marketing_description">추천 코스와 이벤트 소식을 받아요</string>
<!-- en -->
<string name="main_notification_consent_marketing_description">Get recommended courses and event news</string>
<!-- ja -->
<string name="main_notification_consent_marketing_description">おすすめコースやイベント情報を受け取ります</string>
```

### main_notification_consent_marketing_title

```xml
<!-- ko -->
<string name="main_notification_consent_marketing_title">광고 알림</string>
<!-- en -->
<string name="main_notification_consent_marketing_title">Promotional alerts</string>
<!-- ja -->
<string name="main_notification_consent_marketing_title">広告通知</string>
```

### main_notification_consent_night_description

```xml
<!-- ko -->
<string name="main_notification_consent_night_description">밤 9시부터 오전 8시까지도 받아요</string>
<!-- en -->
<string name="main_notification_consent_night_description">Also from 9 PM to 8 AM</string>
<!-- ja -->
<string name="main_notification_consent_night_description">夜9時から朝8時まで受け取ります</string>
```

### main_notification_consent_night_title

```xml
<!-- ko -->
<string name="main_notification_consent_night_title">야간에도 받기</string>
<!-- en -->
<string name="main_notification_consent_night_title">Receive at night too</string>
<!-- ja -->
<string name="main_notification_consent_night_title">夜間も受け取る</string>
```

### main_notification_consent_service_description

```xml
<!-- ko -->
<string name="main_notification_consent_service_description">내 코스의 좋아요와 댓글 소식을 받아요</string>
<!-- en -->
<string name="main_notification_consent_service_description">Get likes and comments on your courses</string>
<!-- ja -->
<string name="main_notification_consent_service_description">自分のコースへのいいねやコメントを受け取ります</string>
```

### main_notification_consent_service_title

```xml
<!-- ko -->
<string name="main_notification_consent_service_title">활동 알림</string>
<!-- en -->
<string name="main_notification_consent_service_title">Activity alerts</string>
<!-- ja -->
<string name="main_notification_consent_service_title">アクティビティ通知</string>
```

### main_notification_consent_subtitle

```xml
<!-- ko -->
<string name="main_notification_consent_subtitle">나중에 설정에서 바꿀 수 있어요.</string>
<!-- en -->
<string name="main_notification_consent_subtitle">You can change this later in settings.</string>
<!-- ja -->
<string name="main_notification_consent_subtitle">後で設定から変更できます。</string>
```

### main_notification_consent_tag_optional

```xml
<!-- ko -->
<string name="main_notification_consent_tag_optional">(선택)</string>
<!-- en -->
<string name="main_notification_consent_tag_optional">(Optional)</string>
<!-- ja -->
<string name="main_notification_consent_tag_optional">（任意）</string>
```

### main_notification_consent_tag_required

```xml
<!-- ko -->
<string name="main_notification_consent_tag_required">(필수)</string>
<!-- en -->
<string name="main_notification_consent_tag_required">(Required)</string>
<!-- ja -->
<string name="main_notification_consent_tag_required">（必須）</string>
```

### main_notification_consent_title

```xml
<!-- ko -->
<string name="main_notification_consent_title">추천 코스 알림을 받아볼까요?</string>
<!-- en -->
<string name="main_notification_consent_title">Get course recommendation alerts?</string>
<!-- ja -->
<string name="main_notification_consent_title">おすすめコースの通知を受け取りますか？</string>
```

### main_notification_inbox_all_read

```xml
<!-- ko -->
<string name="main_notification_inbox_all_read">모든 알림을 읽었어요</string>
<!-- en -->
<string name="main_notification_inbox_all_read">You\'re all caught up</string>
<!-- ja -->
<string name="main_notification_inbox_all_read">すべて既読にしました</string>
```

### main_notification_inbox_empty_description

```xml
<!-- ko -->
<string name="main_notification_inbox_empty_description">내 코스에 남겨진 반응과\n새로운 팔로우 소식을 여기서 확인해요.</string>
<!-- en -->
<string name="main_notification_inbox_empty_description">Reactions on your courses and\nnew follower updates show up here.</string>
<!-- ja -->
<string name="main_notification_inbox_empty_description">自分のコースへの反応や\n新しいフォローのお知らせをここで確認できます。</string>
```

### main_notification_inbox_empty_title

```xml
<!-- ko -->
<string name="main_notification_inbox_empty_title">아직 도착한 알림이 없어요</string>
<!-- en -->
<string name="main_notification_inbox_empty_title">No notifications yet</string>
<!-- ja -->
<string name="main_notification_inbox_empty_title">お知らせはまだありません</string>
```

### main_notification_inbox_error_title

```xml
<!-- ko -->
<string name="main_notification_inbox_error_title">알림을 불러오지 못했어요</string>
<!-- en -->
<string name="main_notification_inbox_error_title">Couldn\'t load notifications</string>
<!-- ja -->
<string name="main_notification_inbox_error_title">お知らせを読み込めませんでした</string>
```

### main_notification_inbox_load_more

```xml
<!-- ko -->
<string name="main_notification_inbox_load_more">이전 알림 더 보기</string>
<!-- en -->
<string name="main_notification_inbox_load_more">Load older</string>
<!-- ja -->
<string name="main_notification_inbox_load_more">以前のお知らせを見る</string>
```

### main_notification_inbox_mark_all_read

```xml
<!-- ko -->
<string name="main_notification_inbox_mark_all_read">모두 읽음</string>
<!-- en -->
<string name="main_notification_inbox_mark_all_read">Mark all read</string>
<!-- ja -->
<string name="main_notification_inbox_mark_all_read">すべて既読</string>
```

### main_notification_inbox_marking_all

```xml
<!-- ko -->
<string name="main_notification_inbox_marking_all">읽는 중…</string>
<!-- en -->
<string name="main_notification_inbox_marking_all">Marking…</string>
<!-- ja -->
<string name="main_notification_inbox_marking_all">処理中…</string>
```

### main_notification_inbox_recent

```xml
<!-- ko -->
<string name="main_notification_inbox_recent">최근 알림</string>
<!-- en -->
<string name="main_notification_inbox_recent">Recent</string>
<!-- ja -->
<string name="main_notification_inbox_recent">最近のお知らせ</string>
```

### main_notification_inbox_refresh

```xml
<!-- ko -->
<string name="main_notification_inbox_refresh">새로고침</string>
<!-- en -->
<string name="main_notification_inbox_refresh">Refresh</string>
<!-- ja -->
<string name="main_notification_inbox_refresh">更新</string>
```

### main_notification_inbox_retry

```xml
<!-- ko -->
<string name="main_notification_inbox_retry">다시 시도</string>
<!-- en -->
<string name="main_notification_inbox_retry">Try again</string>
<!-- ja -->
<string name="main_notification_inbox_retry">再試行</string>
```

### main_notification_inbox_title

```xml
<!-- ko -->
<string name="main_notification_inbox_title">알림</string>
<!-- en -->
<string name="main_notification_inbox_title">Notifications</string>
<!-- ja -->
<string name="main_notification_inbox_title">お知らせ</string>
```

### main_notification_inbox_unread_count

```xml
<!-- ko -->
<string name="main_notification_inbox_unread_count">안 읽은 알림 %1$d개</string>
<!-- en -->
<string name="main_notification_inbox_unread_count">%1$d unread</string>
<!-- ja -->
<string name="main_notification_inbox_unread_count">未読%1$d件</string>
```

### main_notification_priming_allow

```xml
<!-- ko -->
<string name="main_notification_priming_allow">허용</string>
<!-- en -->
<string name="main_notification_priming_allow">Allow</string>
<!-- ja -->
<string name="main_notification_priming_allow">許可</string>
```

### main_notification_priming_description

```xml
<!-- ko -->
<string name="main_notification_priming_description">내 코스의 좋아요·댓글 같은 활동 소식을 알림으로 알려드려요.</string>
<!-- en -->
<string name="main_notification_priming_description">Get notified about likes and comments on your courses.</string>
<!-- ja -->
<string name="main_notification_priming_description">自分のコースへのいいねやコメントなどの通知をお届けします。</string>
```

### main_notification_priming_later

```xml
<!-- ko -->
<string name="main_notification_priming_later">나중에</string>
<!-- en -->
<string name="main_notification_priming_later">Not now</string>
<!-- ja -->
<string name="main_notification_priming_later">あとで</string>
```

### main_notification_priming_title

```xml
<!-- ko -->
<string name="main_notification_priming_title">알림을 받아볼까요?</string>
<!-- en -->
<string name="main_notification_priming_title">Turn on notifications?</string>
<!-- ja -->
<string name="main_notification_priming_title">通知を受け取りますか？</string>
```

### main_notification_settings_activity_description

```xml
<!-- ko -->
<string name="main_notification_settings_activity_description">내 코스의 좋아요와 댓글 소식을 받아요</string>
<!-- en -->
<string name="main_notification_settings_activity_description">Get likes and comments on your courses</string>
<!-- ja -->
<string name="main_notification_settings_activity_description">自分のコースへのいいねやコメントを受け取ります</string>
```

### main_notification_settings_activity_title

```xml
<!-- ko -->
<string name="main_notification_settings_activity_title">활동 알림</string>
<!-- en -->
<string name="main_notification_settings_activity_title">Activity notifications</string>
<!-- ja -->
<string name="main_notification_settings_activity_title">アクティビティ通知</string>
```

### main_notification_settings_consent_caption

```xml
<!-- ko -->
<string name="main_notification_settings_consent_caption">%1$s 동의</string>
<!-- en -->
<string name="main_notification_settings_consent_caption">Agreed on %1$s</string>
<!-- ja -->
<string name="main_notification_settings_consent_caption">%1$s 同意</string>
```

### main_notification_settings_dialog_confirm

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_confirm">기기 설정</string>
<!-- en -->
<string name="main_notification_settings_dialog_confirm">Device settings</string>
<!-- ja -->
<string name="main_notification_settings_dialog_confirm">端末の設定</string>
```

### main_notification_settings_dialog_dismiss

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_dismiss">닫기</string>
<!-- en -->
<string name="main_notification_settings_dialog_dismiss">Close</string>
<!-- ja -->
<string name="main_notification_settings_dialog_dismiss">閉じる</string>
```

### main_notification_settings_dialog_error_description

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_error_description">네트워크 연결을 확인하고 다시 시도해 주세요. 알림을 바로 차단하려면 기기 설정에서 꺼 주세요.</string>
<!-- en -->
<string name="main_notification_settings_dialog_error_description">Check your connection and try again. To block notifications now, turn them off in device settings.</string>
<!-- ja -->
<string name="main_notification_settings_dialog_error_description">接続を確認してもう一度お試しください。すぐに通知を止めるには端末の設定でオフにしてください。</string>
```

### main_notification_settings_dialog_error_title

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_error_title">알림 설정을 완료하지 못했어요</string>
<!-- en -->
<string name="main_notification_settings_dialog_error_title">Couldn\'t finish notification setup</string>
<!-- ja -->
<string name="main_notification_settings_dialog_error_title">通知設定を完了できませんでした</string>
```

### main_notification_settings_dialog_grant_description

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_grant_description">활동 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요.</string>
<!-- en -->
<string name="main_notification_settings_dialog_grant_description">To receive activity notifications, allow Courmy notifications in your device settings.</string>
<!-- ja -->
<string name="main_notification_settings_dialog_grant_description">アクティビティ通知を受け取るには、端末の設定でCourmyの通知を許可してください。</string>
```

### main_notification_settings_dialog_grant_title

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_grant_title">기기 알림을 허용해 주세요</string>
<!-- en -->
<string name="main_notification_settings_dialog_grant_title">Allow device notifications</string>
<!-- ja -->
<string name="main_notification_settings_dialog_grant_title">端末の通知を許可してください</string>
```

### main_notification_settings_dialog_permission_description

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_permission_description">활동 알림을 받으려면 기기 설정에서 Courmy 알림을 허용해 주세요.</string>
<!-- en -->
<string name="main_notification_settings_dialog_permission_description">To get activity notifications, allow Courmy notifications in device settings.</string>
<!-- ja -->
<string name="main_notification_settings_dialog_permission_description">アクティビティ通知を受け取るには、端末の設定でCourmyの通知を許可してください。</string>
```

### main_notification_settings_dialog_permission_title

```xml
<!-- ko -->
<string name="main_notification_settings_dialog_permission_title">기기 알림을 허용해 주세요</string>
<!-- en -->
<string name="main_notification_settings_dialog_permission_title">Allow device notifications</string>
<!-- ja -->
<string name="main_notification_settings_dialog_permission_title">端末の通知を許可してください</string>
```

### main_notification_settings_marketing_description

```xml
<!-- ko -->
<string name="main_notification_settings_marketing_description">추천 코스와 이벤트 소식을 받아요</string>
<!-- en -->
<string name="main_notification_settings_marketing_description">Get recommended courses and event news</string>
<!-- ja -->
<string name="main_notification_settings_marketing_description">おすすめコースやイベント情報を受け取ります</string>
```

### main_notification_settings_marketing_title

```xml
<!-- ko -->
<string name="main_notification_settings_marketing_title">광고 알림</string>
<!-- en -->
<string name="main_notification_settings_marketing_title">Promotional notifications</string>
<!-- ja -->
<string name="main_notification_settings_marketing_title">広告通知</string>
```

### main_notification_settings_night_description

```xml
<!-- ko -->
<string name="main_notification_settings_night_description">밤 9시부터 오전 8시까지도 받아요</string>
<!-- en -->
<string name="main_notification_settings_night_description">Also receive from 9 PM to 8 AM</string>
<!-- ja -->
<string name="main_notification_settings_night_description">午後9時から午前8時までも受け取ります</string>
```

### main_notification_settings_night_title

```xml
<!-- ko -->
<string name="main_notification_settings_night_title">야간에도 받기</string>
<!-- en -->
<string name="main_notification_settings_night_title">Allow at night</string>
<!-- ja -->
<string name="main_notification_settings_night_title">夜間も受け取る</string>
```

### main_notification_settings_open_failed

```xml
<!-- ko -->
<string name="main_notification_settings_open_failed">기기 알림 설정을 열 수 없어요.</string>
<!-- en -->
<string name="main_notification_settings_open_failed">Unable to open notification settings.</string>
<!-- ja -->
<string name="main_notification_settings_open_failed">通知設定を開けませんでした。</string>
```

### main_notification_settings_title

```xml
<!-- ko -->
<string name="main_notification_settings_title">알림</string>
<!-- en -->
<string name="main_notification_settings_title">Notifications</string>
<!-- ja -->
<string name="main_notification_settings_title">通知</string>
```

### main_notification_state_read

```xml
<!-- ko -->
<string name="main_notification_state_read">읽음</string>
<!-- en -->
<string name="main_notification_state_read">Read</string>
<!-- ja -->
<string name="main_notification_state_read">既読</string>
```

### main_notification_state_unread

```xml
<!-- ko -->
<string name="main_notification_state_unread">안 읽음</string>
<!-- en -->
<string name="main_notification_state_unread">Unread</string>
<!-- ja -->
<string name="main_notification_state_unread">未読</string>
```

### main_notification_unread_description

```xml
<!-- ko -->
<string name="main_notification_unread_description">알림, 안 읽은 알림 %1$d개</string>
<!-- en -->
<string name="main_notification_unread_description">Notifications, %1$d unread</string>
<!-- ja -->
<string name="main_notification_unread_description">通知、未読%1$d件</string>
```

### main_onboarding_browse

```xml
<!-- ko -->
<string name="main_onboarding_browse">로그인 없이 둘러보기</string>
<!-- en -->
<string name="main_onboarding_browse">Look around without logging in</string>
<!-- ja -->
<string name="main_onboarding_browse">ログインせずに見てみる</string>
```

### main_onboarding_browse_without_login

```xml
<!-- ko -->
<string name="main_onboarding_browse_without_login">로그인 없이 둘러보기</string>
<!-- en -->
<string name="main_onboarding_browse_without_login">Browse without logging in</string>
<!-- ja -->
<string name="main_onboarding_browse_without_login">ログインせずに見てみる</string>
```

### main_onboarding_complete_default_name

```xml
<!-- ko -->
<string name="main_onboarding_complete_default_name">회원</string>
<!-- en -->
<string name="main_onboarding_complete_default_name">there</string>
<!-- ja -->
<string name="main_onboarding_complete_default_name">ゲスト</string>
```

### main_onboarding_complete_default_nickname

```xml
<!-- ko -->
<string name="main_onboarding_complete_default_nickname">회원</string>
<!-- en -->
<string name="main_onboarding_complete_default_nickname">member</string>
<!-- ja -->
<string name="main_onboarding_complete_default_nickname">会員</string>
```

### main_onboarding_complete_greeting

```xml
<!-- ko -->
<string name="main_onboarding_complete_greeting">가입 완료!\n%1$s님, 환영합니다!</string>
<!-- en -->
<string name="main_onboarding_complete_greeting">You\'re all set!\nWelcome, %1$s!</string>
<!-- ja -->
<string name="main_onboarding_complete_greeting">登録完了！\n%1$sさん、ようこそ！</string>
```

### main_onboarding_complete_guide_create_desc

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_create_desc">가 본 장소를 순서대로 엮어 코스로 남겨요.</string>
<!-- en -->
<string name="main_onboarding_complete_guide_create_desc">String together the places you\'ve been into a course.</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_create_desc">訪れた場所を順番につないでコースに残せます。</string>
```

### main_onboarding_complete_guide_create_title

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_create_title">나만의 코스 만들기</string>
<!-- en -->
<string name="main_onboarding_complete_guide_create_title">Create your own course</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_create_title">自分だけのコースを作る</string>
```

### main_onboarding_complete_guide_follow_desc

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_follow_desc">관심 있는 사람의 새 코스를 놓치지 않아요.</string>
<!-- en -->
<string name="main_onboarding_complete_guide_follow_desc">Never miss a new course from people you follow.</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_follow_desc">気になる人の新しいコースを見逃しません。</string>
```

### main_onboarding_complete_guide_follow_title

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_follow_title">취향 맞는 유저 팔로우</string>
<!-- en -->
<string name="main_onboarding_complete_guide_follow_title">Follow users you vibe with</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_follow_title">好みの合うユーザーをフォロー</string>
```

### main_onboarding_complete_guide_header

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_header">이제 이런 걸 할 수 있어요</string>
<!-- en -->
<string name="main_onboarding_complete_guide_header">Here\'s what you can do now</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_header">これからこんなことができます</string>
```

### main_onboarding_complete_guide_heading

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_heading">이제 이런 걸 할 수 있어요</string>
<!-- en -->
<string name="main_onboarding_complete_guide_heading">Here\'s what you can do now</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_heading">これからこんなことができます</string>
```

### main_onboarding_complete_guide_save_desc

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_save_desc">저장한 코스는 저장 탭에서 다시 볼 수 있어요.</string>
<!-- en -->
<string name="main_onboarding_complete_guide_save_desc">Find saved courses again on the Saved tab.</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_save_desc">保存したコースは保存タブでまた見られます。</string>
```

### main_onboarding_complete_guide_save_title

```xml
<!-- ko -->
<string name="main_onboarding_complete_guide_save_title">마음에 든 코스 저장</string>
<!-- en -->
<string name="main_onboarding_complete_guide_save_title">Save courses you like</string>
<!-- ja -->
<string name="main_onboarding_complete_guide_save_title">気に入ったコースを保存</string>
```

### main_onboarding_complete_start_button

```xml
<!-- ko -->
<string name="main_onboarding_complete_start_button">Courmy 시작하기</string>
<!-- en -->
<string name="main_onboarding_complete_start_button">Start Courmy</string>
<!-- ja -->
<string name="main_onboarding_complete_start_button">Courmyを始める</string>
```

### main_onboarding_heading_1

```xml
<!-- ko -->
<string name="main_onboarding_heading_1">저장만 해둔 장소,\n그대로 두고 계신가요?</string>
<!-- en -->
<string name="main_onboarding_heading_1">Still leaving\nyour saved places untouched?</string>
<!-- ja -->
<string name="main_onboarding_heading_1">保存したままの場所、\nそのままにしていませんか?</string>
```

### main_onboarding_heading_2

```xml
<!-- ko -->
<string name="main_onboarding_heading_2">저장한 장소들이\n걷고 싶은 코스가 돼요</string>
<!-- en -->
<string name="main_onboarding_heading_2">Your saved places become\na course worth walking</string>
<!-- ja -->
<string name="main_onboarding_heading_2">保存した場所が\n歩きたくなるコースに</string>
```

### main_onboarding_heading_3

```xml
<!-- ko -->
<string name="main_onboarding_heading_3">함께 나누는\n로컬 큐레이션</string>
<!-- en -->
<string name="main_onboarding_heading_3">Local curation,\nshared together</string>
<!-- ja -->
<string name="main_onboarding_heading_3">みんなで分け合う\nローカルキュレーション</string>
```

### main_onboarding_login

```xml
<!-- ko -->
<string name="main_onboarding_login">로그인 하기</string>
<!-- en -->
<string name="main_onboarding_login">Log in</string>
<!-- ja -->
<string name="main_onboarding_login">ログイン</string>
```

### main_onboarding_next

```xml
<!-- ko -->
<string name="main_onboarding_next">다음</string>
<!-- en -->
<string name="main_onboarding_next">Next</string>
<!-- ja -->
<string name="main_onboarding_next">次へ</string>
```

### main_onboarding_skip

```xml
<!-- ko -->
<string name="main_onboarding_skip">건너뛰기</string>
<!-- en -->
<string name="main_onboarding_skip">Skip</string>
<!-- ja -->
<string name="main_onboarding_skip">スキップ</string>
```

### main_optional_app_update_confirm

```xml
<!-- ko -->
<string name="main_optional_app_update_confirm">업데이트</string>
<!-- en -->
<string name="main_optional_app_update_confirm">Update</string>
<!-- ja -->
<string name="main_optional_app_update_confirm">アップデート</string>
```

### main_optional_app_update_description

```xml
<!-- ko -->
<string name="main_optional_app_update_description">더 나아진 Courmy를 만나보세요.\n마이 &gt; 설정 &gt; 앱 버전에서도 확인할 수 있어요.</string>
<!-- en -->
<string name="main_optional_app_update_description">Discover an improved Courmy.\nYou can also check under My &gt; Settings &gt; App version.</string>
<!-- ja -->
<string name="main_optional_app_update_description">もっと便利になったCourmyをお試しください。\nマイ &gt; 設定 &gt; アプリバージョンからも確認できます。</string>
```

### main_optional_app_update_dismiss

```xml
<!-- ko -->
<string name="main_optional_app_update_dismiss">나중에</string>
<!-- en -->
<string name="main_optional_app_update_dismiss">Later</string>
<!-- ja -->
<string name="main_optional_app_update_dismiss">後で</string>
```

### main_optional_app_update_title

```xml
<!-- ko -->
<string name="main_optional_app_update_title">새 버전이 있어요</string>
<!-- en -->
<string name="main_optional_app_update_title">A new version is available</string>
<!-- ja -->
<string name="main_optional_app_update_title">新しいバージョンがあります</string>
```

### main_profile_avatar_empty_desc

```xml
<!-- ko -->
<string name="main_profile_avatar_empty_desc">프로필 이미지 없음</string>
<!-- en -->
<string name="main_profile_avatar_empty_desc">No profile image</string>
<!-- ja -->
<string name="main_profile_avatar_empty_desc">プロフィール画像なし</string>
```

### main_profile_avatar_image_desc

```xml
<!-- ko -->
<string name="main_profile_avatar_image_desc">프로필 이미지</string>
<!-- en -->
<string name="main_profile_avatar_image_desc">Profile image</string>
<!-- ja -->
<string name="main_profile_avatar_image_desc">プロフィール画像</string>
```

### main_profile_bio_empty

```xml
<!-- ko -->
<string name="main_profile_bio_empty">아직 소개가 없어요</string>
<!-- en -->
<string name="main_profile_bio_empty">No bio yet</string>
<!-- ja -->
<string name="main_profile_bio_empty">まだ自己紹介がありません</string>
```

### main_profile_edit_avatar_change_desc

```xml
<!-- ko -->
<string name="main_profile_edit_avatar_change_desc">사진 변경</string>
<!-- en -->
<string name="main_profile_edit_avatar_change_desc">Change photo</string>
<!-- ja -->
<string name="main_profile_edit_avatar_change_desc">写真を変更</string>
```

### main_profile_edit_avatar_image_desc

```xml
<!-- ko -->
<string name="main_profile_edit_avatar_image_desc">프로필 이미지</string>
<!-- en -->
<string name="main_profile_edit_avatar_image_desc">Profile photo</string>
<!-- ja -->
<string name="main_profile_edit_avatar_image_desc">プロフィール画像</string>
```

### main_profile_edit_handle_check

```xml
<!-- ko -->
<string name="main_profile_edit_handle_check">중복 확인</string>
<!-- en -->
<string name="main_profile_edit_handle_check">Check</string>
<!-- ja -->
<string name="main_profile_edit_handle_check">重複確認</string>
```

### main_profile_edit_interest_edit

```xml
<!-- ko -->
<string name="main_profile_edit_interest_edit">편집</string>
<!-- en -->
<string name="main_profile_edit_interest_edit">Edit</string>
<!-- ja -->
<string name="main_profile_edit_interest_edit">編集</string>
```

### main_profile_edit_interest_empty

```xml
<!-- ko -->
<string name="main_profile_edit_interest_empty">아직 고른 %1$s 가 없어요. 편집에서 골라 보세요.</string>
<!-- en -->
<string name="main_profile_edit_interest_empty">No %1$s selected yet. Tap Edit to choose.</string>
<!-- ja -->
<string name="main_profile_edit_interest_empty">%1$s がまだ選ばれていません。編集から選んでみましょう。</string>
```

### main_profile_edit_interest_regions

```xml
<!-- ko -->
<string name="main_profile_edit_interest_regions">관심 지역</string>
<!-- en -->
<string name="main_profile_edit_interest_regions">Favorite areas</string>
<!-- ja -->
<string name="main_profile_edit_interest_regions">興味のある地域</string>
```

### main_profile_edit_interest_themes

```xml
<!-- ko -->
<string name="main_profile_edit_interest_themes">관심 테마</string>
<!-- en -->
<string name="main_profile_edit_interest_themes">Interests</string>
<!-- ja -->
<string name="main_profile_edit_interest_themes">興味のあるテーマ</string>
```

### main_profile_edit_label_bio

```xml
<!-- ko -->
<string name="main_profile_edit_label_bio">소개</string>
<!-- en -->
<string name="main_profile_edit_label_bio">Bio</string>
<!-- ja -->
<string name="main_profile_edit_label_bio">自己紹介</string>
```

### main_profile_edit_label_handle

```xml
<!-- ko -->
<string name="main_profile_edit_label_handle">아이디</string>
<!-- en -->
<string name="main_profile_edit_label_handle">ID</string>
<!-- ja -->
<string name="main_profile_edit_label_handle">ID</string>
```

### main_profile_edit_label_nickname

```xml
<!-- ko -->
<string name="main_profile_edit_label_nickname">닉네임</string>
<!-- en -->
<string name="main_profile_edit_label_nickname">Nickname</string>
<!-- ja -->
<string name="main_profile_edit_label_nickname">ニックネーム</string>
```

### main_profile_edit_save

```xml
<!-- ko -->
<string name="main_profile_edit_save">변경 사항 저장</string>
<!-- en -->
<string name="main_profile_edit_save">Save changes</string>
<!-- ja -->
<string name="main_profile_edit_save">変更を保存</string>
```

### main_profile_edit_title

```xml
<!-- ko -->
<string name="main_profile_edit_title">프로필 편집</string>
<!-- en -->
<string name="main_profile_edit_title">Edit profile</string>
<!-- ja -->
<string name="main_profile_edit_title">プロフィール編集</string>
```

### main_profile_edit_toast_saved

```xml
<!-- ko -->
<string name="main_profile_edit_toast_saved">저장했어요</string>
<!-- en -->
<string name="main_profile_edit_toast_saved">Saved</string>
<!-- ja -->
<string name="main_profile_edit_toast_saved">保存しました</string>
```

### main_profile_error_go_home

```xml
<!-- ko -->
<string name="main_profile_error_go_home">홈으로 돌아가기</string>
<!-- en -->
<string name="main_profile_error_go_home">Back to home</string>
<!-- ja -->
<string name="main_profile_error_go_home">ホームに戻る</string>
```

### main_profile_error_message

```xml
<!-- ko -->
<string name="main_profile_error_message">프로필을 불러오지 못했습니다.</string>
<!-- en -->
<string name="main_profile_error_message">Couldn\'t load the profile.</string>
<!-- ja -->
<string name="main_profile_error_message">プロフィールを読み込めませんでした。</string>
```

### main_profile_error_retry

```xml
<!-- ko -->
<string name="main_profile_error_retry">다시 시도</string>
<!-- en -->
<string name="main_profile_error_retry">Try again</string>
<!-- ja -->
<string name="main_profile_error_retry">再試行</string>
```

### main_profile_follower

```xml
<!-- ko -->
<string name="main_profile_follower">팔로워</string>
<!-- en -->
<string name="main_profile_follower">Followers</string>
<!-- ja -->
<string name="main_profile_follower">フォロワー</string>
```

### main_profile_following

```xml
<!-- ko -->
<string name="main_profile_following">팔로잉</string>
<!-- en -->
<string name="main_profile_following">Following</string>
<!-- ja -->
<string name="main_profile_following">フォロー中</string>
```

### main_profile_header_back

```xml
<!-- ko -->
<string name="main_profile_header_back">뒤로 가기</string>
<!-- en -->
<string name="main_profile_header_back">Back</string>
<!-- ja -->
<string name="main_profile_header_back">戻る</string>
```

### main_profile_setup_add_photo

```xml
<!-- ko -->
<string name="main_profile_setup_add_photo">사진 추가</string>
<!-- en -->
<string name="main_profile_setup_add_photo">Add photo</string>
<!-- ja -->
<string name="main_profile_setup_add_photo">写真を追加</string>
```

### main_profile_setup_avatar_add_desc

```xml
<!-- ko -->
<string name="main_profile_setup_avatar_add_desc">사진 추가</string>
<!-- en -->
<string name="main_profile_setup_avatar_add_desc">Add photo</string>
<!-- ja -->
<string name="main_profile_setup_avatar_add_desc">写真を追加</string>
```

### main_profile_setup_avatar_description

```xml
<!-- ko -->
<string name="main_profile_setup_avatar_description">프로필 이미지</string>
<!-- en -->
<string name="main_profile_setup_avatar_description">Profile image</string>
<!-- ja -->
<string name="main_profile_setup_avatar_description">プロフィール画像</string>
```

### main_profile_setup_avatar_image_desc

```xml
<!-- ko -->
<string name="main_profile_setup_avatar_image_desc">프로필 이미지</string>
<!-- en -->
<string name="main_profile_setup_avatar_image_desc">Profile image</string>
<!-- ja -->
<string name="main_profile_setup_avatar_image_desc">プロフィール画像</string>
```

### main_profile_setup_check_duplicate

```xml
<!-- ko -->
<string name="main_profile_setup_check_duplicate">중복 확인</string>
<!-- en -->
<string name="main_profile_setup_check_duplicate">Check</string>
<!-- ja -->
<string name="main_profile_setup_check_duplicate">重複確認</string>
```

### main_profile_setup_handle_check

```xml
<!-- ko -->
<string name="main_profile_setup_handle_check">중복 확인</string>
<!-- en -->
<string name="main_profile_setup_handle_check">Check</string>
<!-- ja -->
<string name="main_profile_setup_handle_check">重複確認</string>
```

### main_profile_setup_handle_label

```xml
<!-- ko -->
<string name="main_profile_setup_handle_label">아이디</string>
<!-- en -->
<string name="main_profile_setup_handle_label">ID</string>
<!-- ja -->
<string name="main_profile_setup_handle_label">ID</string>
```

### main_profile_setup_handle_placeholder

```xml
<!-- ko -->
<string name="main_profile_setup_handle_placeholder">아이디</string>
<!-- en -->
<string name="main_profile_setup_handle_placeholder">ID</string>
<!-- ja -->
<string name="main_profile_setup_handle_placeholder">ID</string>
```

### main_profile_setup_label_handle

```xml
<!-- ko -->
<string name="main_profile_setup_label_handle">아이디</string>
<!-- en -->
<string name="main_profile_setup_label_handle">Handle</string>
<!-- ja -->
<string name="main_profile_setup_label_handle">ID</string>
```

### main_profile_setup_label_nickname

```xml
<!-- ko -->
<string name="main_profile_setup_label_nickname">닉네임</string>
<!-- en -->
<string name="main_profile_setup_label_nickname">Nickname</string>
<!-- ja -->
<string name="main_profile_setup_label_nickname">ニックネーム</string>
```

### main_profile_setup_next

```xml
<!-- ko -->
<string name="main_profile_setup_next">다음</string>
<!-- en -->
<string name="main_profile_setup_next">Next</string>
<!-- ja -->
<string name="main_profile_setup_next">次へ</string>
```

### main_profile_setup_nickname_label

```xml
<!-- ko -->
<string name="main_profile_setup_nickname_label">닉네임</string>
<!-- en -->
<string name="main_profile_setup_nickname_label">Nickname</string>
<!-- ja -->
<string name="main_profile_setup_nickname_label">ニックネーム</string>
```

### main_profile_setup_nickname_placeholder

```xml
<!-- ko -->
<string name="main_profile_setup_nickname_placeholder">닉네임을 입력해 주세요</string>
<!-- en -->
<string name="main_profile_setup_nickname_placeholder">Enter your nickname</string>
<!-- ja -->
<string name="main_profile_setup_nickname_placeholder">ニックネームを入力してください</string>
```

### main_profile_setup_title

```xml
<!-- ko -->
<string name="main_profile_setup_title">프로필을\n만들어 주세요</string>
<!-- en -->
<string name="main_profile_setup_title">Create your\nprofile</string>
<!-- ja -->
<string name="main_profile_setup_title">プロフィールを\n作成しましょう</string>
```

### main_recent_keyword_chip_remove_description

```xml
<!-- ko -->
<string name="main_recent_keyword_chip_remove_description">%1$s 검색 기록 삭제</string>
<!-- en -->
<string name="main_recent_keyword_chip_remove_description">Remove search history for %1$s</string>
<!-- ja -->
<string name="main_recent_keyword_chip_remove_description">%1$sの検索履歴を削除</string>
```

### main_recommended_back

```xml
<!-- ko -->
<string name="main_recommended_back">뒤로가기</string>
<!-- en -->
<string name="main_recommended_back">Back</string>
<!-- ja -->
<string name="main_recommended_back">戻る</string>
```

### main_recommended_empty

```xml
<!-- ko -->
<string name="main_recommended_empty">아직 공개된 코스가 없어요.</string>
<!-- en -->
<string name="main_recommended_empty">No public courses yet.</string>
<!-- ja -->
<string name="main_recommended_empty">公開されたコースはまだありません。</string>
```

### main_recommended_empty_theme

```xml
<!-- ko -->
<string name="main_recommended_empty_theme">해당 테마의 코스가 아직 없어요.</string>
<!-- en -->
<string name="main_recommended_empty_theme">No courses in this theme yet.</string>
<!-- ja -->
<string name="main_recommended_empty_theme">このテーマのコースはまだありません。</string>
```

### main_recommended_region_title

```xml
<!-- ko -->
<string name="main_recommended_region_title">%1$s 코스</string>
<!-- en -->
<string name="main_recommended_region_title">%1$s courses</string>
<!-- ja -->
<string name="main_recommended_region_title">%1$sのコース</string>
```

### main_recommended_retry

```xml
<!-- ko -->
<string name="main_recommended_retry">다시 시도</string>
<!-- en -->
<string name="main_recommended_retry">Retry</string>
<!-- ja -->
<string name="main_recommended_retry">再試行</string>
```

### main_recommended_sort_default

```xml
<!-- ko -->
<string name="main_recommended_sort_default">추천순</string>
<!-- en -->
<string name="main_recommended_sort_default">Recommended</string>
<!-- ja -->
<string name="main_recommended_sort_default">おすすめ順</string>
```

### main_recommended_title

```xml
<!-- ko -->
<string name="main_recommended_title">오늘의 추천 코스</string>
<!-- en -->
<string name="main_recommended_title">Today\'s picks</string>
<!-- ja -->
<string name="main_recommended_title">今日のおすすめコース</string>
```

### main_region_map_content_description

```xml
<!-- ko -->
<string name="main_region_map_content_description">%1$s 행정구역 지도. 아래 지역 목록에서도 선택할 수 있습니다.</string>
<!-- en -->
<string name="main_region_map_content_description">Administrative district map of %1$s. You can also select from the region list below.</string>
<!-- ja -->
<string name="main_region_map_content_description">%1$sの行政区域マップ。下の地域リストからも選択できます。</string>
```

### main_region_map_country_name

```xml
<!-- ko -->
<string name="main_region_map_country_name">대한민국</string>
<!-- en -->
<string name="main_region_map_country_name">Korea</string>
<!-- ja -->
<string name="main_region_map_country_name">韓国</string>
```

### main_region_map_inset_east_islands

```xml
<!-- ko -->
<string name="main_region_map_inset_east_islands">울릉도·독도</string>
<!-- en -->
<string name="main_region_map_inset_east_islands">Ulleungdo·Dokdo</string>
<!-- ja -->
<string name="main_region_map_inset_east_islands">鬱陵島·独島</string>
```

### main_region_map_inset_separate_scale

```xml
<!-- ko -->
<string name="main_region_map_inset_separate_scale">별도 축척</string>
<!-- en -->
<string name="main_region_map_inset_separate_scale">Not to scale</string>
<!-- ja -->
<string name="main_region_map_inset_separate_scale">縮尺は目安</string>
```

### main_region_map_inset_west_islands

```xml
<!-- ko -->
<string name="main_region_map_inset_west_islands">백령·대청·연평</string>
<!-- en -->
<string name="main_region_map_inset_west_islands">Baengnyeong·Daecheong·Yeonpyeong</string>
<!-- ja -->
<string name="main_region_map_inset_west_islands">白翎·大青·延坪</string>
```

### main_remaining_actor_unknown

```xml
<!-- ko -->
<string name="main_remaining_actor_unknown">누군가</string>
<!-- en -->
<string name="main_remaining_actor_unknown">Someone</string>
<!-- ja -->
<string name="main_remaining_actor_unknown">誰か</string>
```

### main_remaining_brand_morph

```xml
<!-- ko -->
<string name="main_remaining_brand_morph">my course</string>
<!-- en -->
<string name="main_remaining_brand_morph">my course</string>
<!-- ja -->
<string name="main_remaining_brand_morph">my course</string>
```

### main_remaining_brand_wordmark

```xml
<!-- ko -->
<string name="main_remaining_brand_wordmark">Courmy</string>
<!-- en -->
<string name="main_remaining_brand_wordmark">Courmy</string>
<!-- ja -->
<string name="main_remaining_brand_wordmark">Courmy</string>
```

### main_remaining_chat_days

```xml
<!-- ko -->
<plurals name="main_remaining_chat_days">
        <item quantity="other">%d일 전</item>
    </plurals>
<!-- en -->
<plurals name="main_remaining_chat_days">
        <item quantity="one">%d day ago</item>
        <item quantity="other">%d days ago</item>
    </plurals>
<!-- ja -->
<plurals name="main_remaining_chat_days">
        <item quantity="other">%d日前</item>
    </plurals>
```

### main_remaining_chat_now

```xml
<!-- ko -->
<string name="main_remaining_chat_now">방금</string>
<!-- en -->
<string name="main_remaining_chat_now">Just now</string>
<!-- ja -->
<string name="main_remaining_chat_now">たった今</string>
```

### main_remaining_chat_yesterday

```xml
<!-- ko -->
<string name="main_remaining_chat_yesterday">어제</string>
<!-- en -->
<string name="main_remaining_chat_yesterday">Yesterday</string>
<!-- ja -->
<string name="main_remaining_chat_yesterday">昨日</string>
```

### main_remaining_keyword_new

```xml
<!-- ko -->
<string name="main_remaining_keyword_new">NEW</string>
<!-- en -->
<string name="main_remaining_keyword_new">NEW</string>
<!-- ja -->
<string name="main_remaining_keyword_new">新着</string>
```

### main_remaining_notification_commented

```xml
<!-- ko -->
<string name="main_remaining_notification_commented">%1$s님이 내 코스에 댓글을 남겼어요</string>
<!-- en -->
<string name="main_remaining_notification_commented">%1$s commented on your course</string>
<!-- ja -->
<string name="main_remaining_notification_commented">%1$sさんがあなたのコースにコメントしました</string>
```

### main_remaining_notification_date_pattern

```xml
<!-- ko -->
<string name="main_remaining_notification_date_pattern">M월 d일</string>
<!-- en -->
<string name="main_remaining_notification_date_pattern">MMM d</string>
<!-- ja -->
<string name="main_remaining_notification_date_pattern">M月d日</string>
```

### main_remaining_notification_followed

```xml
<!-- ko -->
<string name="main_remaining_notification_followed">%1$s님이 나를 팔로우해요</string>
<!-- en -->
<string name="main_remaining_notification_followed">%1$s followed you</string>
<!-- ja -->
<string name="main_remaining_notification_followed">%1$sさんがあなたをフォローしました</string>
```

### main_remaining_notification_hours

```xml
<!-- ko -->
<plurals name="main_remaining_notification_hours">
        <item quantity="other">%d시간 전</item>
    </plurals>
<!-- en -->
<plurals name="main_remaining_notification_hours">
        <item quantity="one">%d hour ago</item>
        <item quantity="other">%d hours ago</item>
    </plurals>
<!-- ja -->
<plurals name="main_remaining_notification_hours">
        <item quantity="other">%d時間前</item>
    </plurals>
```

### main_remaining_notification_liked

```xml
<!-- ko -->
<string name="main_remaining_notification_liked">%1$s님이 내 코스를 좋아해요</string>
<!-- en -->
<string name="main_remaining_notification_liked">%1$s liked your course</string>
<!-- ja -->
<string name="main_remaining_notification_liked">%1$sさんがあなたのコースにいいねしました</string>
```

### main_remaining_notification_minutes

```xml
<!-- ko -->
<plurals name="main_remaining_notification_minutes">
        <item quantity="other">%d분 전</item>
    </plurals>
<!-- en -->
<plurals name="main_remaining_notification_minutes">
        <item quantity="one">%d minute ago</item>
        <item quantity="other">%d minutes ago</item>
    </plurals>
<!-- ja -->
<plurals name="main_remaining_notification_minutes">
        <item quantity="other">%d分前</item>
    </plurals>
```

### main_remaining_notification_now

```xml
<!-- ko -->
<string name="main_remaining_notification_now">방금 전</string>
<!-- en -->
<string name="main_remaining_notification_now">Just now</string>
<!-- ja -->
<string name="main_remaining_notification_now">たった今</string>
```

### main_remaining_notification_reviewed

```xml
<!-- ko -->
<string name="main_remaining_notification_reviewed">%1$s님이 내 코스에 후기를 남겼어요</string>
<!-- en -->
<string name="main_remaining_notification_reviewed">%1$s reviewed your course</string>
<!-- ja -->
<string name="main_remaining_notification_reviewed">%1$sさんがあなたのコースにレビューを書きました</string>
```

### main_remaining_notification_unknown

```xml
<!-- ko -->
<string name="main_remaining_notification_unknown">새로운 소식이 도착했어요</string>
<!-- en -->
<string name="main_remaining_notification_unknown">You have a new update</string>
<!-- ja -->
<string name="main_remaining_notification_unknown">新しいお知らせがあります</string>
```

### main_remaining_place_count

```xml
<!-- ko -->
<plurals name="main_remaining_place_count">
        <item quantity="other">%1$d곳</item>
    </plurals>
<!-- en -->
<plurals name="main_remaining_place_count">
        <item quantity="one">%1$d place</item>
        <item quantity="other">%1$d places</item>
    </plurals>
<!-- ja -->
<plurals name="main_remaining_place_count">
        <item quantity="other">%1$dか所</item>
    </plurals>
```

### main_saved_course_card_completed

```xml
<!-- ko -->
<string name="main_saved_course_card_completed">%1$s 완주</string>
<!-- en -->
<string name="main_saved_course_card_completed">Completed %1$s</string>
<!-- ja -->
<string name="main_saved_course_card_completed">%1$s 完走</string>
```

### main_saved_course_card_mine

```xml
<!-- ko -->
<string name="main_saved_course_card_mine">내가 만든 코스</string>
<!-- en -->
<string name="main_saved_course_card_mine">My course</string>
<!-- ja -->
<string name="main_saved_course_card_mine">自分のコース</string>
```

### main_saved_course_card_unsave

```xml
<!-- ko -->
<string name="main_saved_course_card_unsave">저장 취소</string>
<!-- en -->
<string name="main_saved_course_card_unsave">Remove from saved</string>
<!-- ja -->
<string name="main_saved_course_card_unsave">保存を解除</string>
```

### main_saved_courses_empty

```xml
<!-- ko -->
<string name="main_saved_courses_empty">조건에 맞는 저장 코스가 없어요.</string>
<!-- en -->
<string name="main_saved_courses_empty">No saved courses match your filters.</string>
<!-- ja -->
<string name="main_saved_courses_empty">条件に合う保存済みコースがありません。</string>
```

### main_saved_create_folder_cancel

```xml
<!-- ko -->
<string name="main_saved_create_folder_cancel">취소</string>
<!-- en -->
<string name="main_saved_create_folder_cancel">Cancel</string>
<!-- ja -->
<string name="main_saved_create_folder_cancel">キャンセル</string>
```

### main_saved_create_folder_confirm

```xml
<!-- ko -->
<string name="main_saved_create_folder_confirm">추가</string>
<!-- en -->
<string name="main_saved_create_folder_confirm">Add</string>
<!-- ja -->
<string name="main_saved_create_folder_confirm">追加</string>
```

### main_saved_create_folder_description

```xml
<!-- ko -->
<string name="main_saved_create_folder_description">원하는 이름으로 코스를 모아보세요. (최대 %1$d자)</string>
<!-- en -->
<string name="main_saved_create_folder_description">Group courses under a name you like. (up to %1$d characters)</string>
<!-- ja -->
<string name="main_saved_create_folder_description">好きな名前でコースをまとめましょう。(最大 %1$d 文字)</string>
```

### main_saved_create_folder_placeholder

```xml
<!-- ko -->
<string name="main_saved_create_folder_placeholder">예: 주말 나들이</string>
<!-- en -->
<string name="main_saved_create_folder_placeholder">e.g. Weekend outings</string>
<!-- ja -->
<string name="main_saved_create_folder_placeholder">例:週末のおでかけ</string>
```

### main_saved_create_folder_title

```xml
<!-- ko -->
<string name="main_saved_create_folder_title">폴더 추가</string>
<!-- en -->
<string name="main_saved_create_folder_title">Add folder</string>
<!-- ja -->
<string name="main_saved_create_folder_title">フォルダを追加</string>
```

### main_saved_filter_add_folder

```xml
<!-- ko -->
<string name="main_saved_filter_add_folder">폴더 추가</string>
<!-- en -->
<string name="main_saved_filter_add_folder">Add folder</string>
<!-- ja -->
<string name="main_saved_filter_add_folder">フォルダを追加</string>
```

### main_saved_filter_all

```xml
<!-- ko -->
<string name="main_saved_filter_all">전체 %1$d</string>
<!-- en -->
<string name="main_saved_filter_all">All %1$d</string>
<!-- ja -->
<string name="main_saved_filter_all">すべて %1$d</string>
```

### main_saved_filter_chip_label

```xml
<!-- ko -->
<string name="main_saved_filter_chip_label">%1$s %2$d</string>
<!-- en -->
<string name="main_saved_filter_chip_label">%1$s %2$d</string>
<!-- ja -->
<string name="main_saved_filter_chip_label">%1$s %2$d</string>
```

### main_saved_filter_completed

```xml
<!-- ko -->
<string name="main_saved_filter_completed">완주 %1$d</string>
<!-- en -->
<string name="main_saved_filter_completed">Done %1$d</string>
<!-- ja -->
<string name="main_saved_filter_completed">踏破 %1$d</string>
```

### main_saved_filter_uncompleted

```xml
<!-- ko -->
<string name="main_saved_filter_uncompleted">안 가봄 %1$d</string>
<!-- en -->
<string name="main_saved_filter_uncompleted">To go %1$d</string>
<!-- ja -->
<string name="main_saved_filter_uncompleted">未訪問 %1$d</string>
```

### main_saved_guest_login

```xml
<!-- ko -->
<string name="main_saved_guest_login">로그인 / 회원가입</string>
<!-- en -->
<string name="main_saved_guest_login">Log in / Sign up</string>
<!-- ja -->
<string name="main_saved_guest_login">ログイン / 新規登録</string>
```

### main_saved_guest_subtitle

```xml
<!-- ko -->
<string name="main_saved_guest_subtitle">로그인하면 저장한 장소가 지도 위에 모이고,\n바로 나만의 코스로 이어집니다.</string>
<!-- en -->
<string name="main_saved_guest_subtitle">Sign in to see your saved places on the map\nand turn them into your own course.</string>
<!-- ja -->
<string name="main_saved_guest_subtitle">ログインすると保存したスポットが地図に集まり、\nそのまま自分だけのコースに繋がります。</string>
```

### main_saved_guest_title

```xml
<!-- ko -->
<string name="main_saved_guest_title">마음에 든 장소를\n여기에 모아보세요</string>
<!-- en -->
<string name="main_saved_guest_title">Collect the places\nyou love right here</string>
<!-- ja -->
<string name="main_saved_guest_title">お気に入りのスポットを\nここにまとめましょう</string>
```

### main_saved_place_card_remove_bookmark

```xml
<!-- ko -->
<string name="main_saved_place_card_remove_bookmark">저장 취소</string>
<!-- en -->
<string name="main_saved_place_card_remove_bookmark">Remove bookmark</string>
<!-- ja -->
<string name="main_saved_place_card_remove_bookmark">保存を解除</string>
```

### main_saved_place_card_visited

```xml
<!-- ko -->
<string name="main_saved_place_card_visited">방문함</string>
<!-- en -->
<string name="main_saved_place_card_visited">Visited</string>
<!-- ja -->
<string name="main_saved_place_card_visited">訪問済み</string>
```

### main_saved_places_empty

```xml
<!-- ko -->
<string name="main_saved_places_empty">조건에 맞는 저장 장소가 없어요.</string>
<!-- en -->
<string name="main_saved_places_empty">No saved places match your filters.</string>
<!-- ja -->
<string name="main_saved_places_empty">条件に合う保存済みスポットがありません。</string>
```

### main_saved_places_load_more

```xml
<!-- ko -->
<string name="main_saved_places_load_more">장소 더 불러오기</string>
<!-- en -->
<string name="main_saved_places_load_more">Load more places</string>
<!-- ja -->
<string name="main_saved_places_load_more">スポットをもっと見る</string>
```

### main_saved_places_loaded_notice

```xml
<!-- ko -->
<string name="main_saved_places_loaded_notice">불러온 장소 기준</string>
<!-- en -->
<string name="main_saved_places_loaded_notice">Among loaded places</string>
<!-- ja -->
<string name="main_saved_places_loaded_notice">読み込んだスポットが対象</string>
```

### main_saved_retry

```xml
<!-- ko -->
<string name="main_saved_retry">다시 시도</string>
<!-- en -->
<string name="main_saved_retry">Try again</string>
<!-- ja -->
<string name="main_saved_retry">再試行</string>
```

### main_saved_status_not_visited

```xml
<!-- ko -->
<string name="main_saved_status_not_visited">미방문 %1$d</string>
<!-- en -->
<string name="main_saved_status_not_visited">Not visited %1$d</string>
<!-- ja -->
<string name="main_saved_status_not_visited">未訪問 %1$d</string>
```

### main_saved_status_visited

```xml
<!-- ko -->
<string name="main_saved_status_visited">방문 %1$d</string>
<!-- en -->
<string name="main_saved_status_visited">Visited %1$d</string>
<!-- ja -->
<string name="main_saved_status_visited">訪問済み %1$d</string>
```

### main_saved_tab_courses

```xml
<!-- ko -->
<string name="main_saved_tab_courses">코스</string>
<!-- en -->
<string name="main_saved_tab_courses">Courses</string>
<!-- ja -->
<string name="main_saved_tab_courses">コース</string>
```

### main_saved_tab_places

```xml
<!-- ko -->
<string name="main_saved_tab_places">장소</string>
<!-- en -->
<string name="main_saved_tab_places">Places</string>
<!-- ja -->
<string name="main_saved_tab_places">スポット</string>
```

### main_saved_unsave_course_dialog_description

```xml
<!-- ko -->
<string name="main_saved_unsave_course_dialog_description">저장을 취소하면 이 코스가 저장 목록에서 사라져요.</string>
<!-- en -->
<string name="main_saved_unsave_course_dialog_description">This course will be removed from your saved list.</string>
<!-- ja -->
<string name="main_saved_unsave_course_dialog_description">解除すると、このコースが保存リストから消えます。</string>
```

### main_saved_unsave_dialog_title

```xml
<!-- ko -->
<string name="main_saved_unsave_dialog_title">저장을 취소할까요?</string>
<!-- en -->
<string name="main_saved_unsave_dialog_title">Remove from saved?</string>
<!-- ja -->
<string name="main_saved_unsave_dialog_title">保存を解除しますか？</string>
```

### main_saved_unsave_place_dialog_description

```xml
<!-- ko -->
<string name="main_saved_unsave_place_dialog_description">저장을 취소하면 이 장소가 저장 목록에서 사라져요.</string>
<!-- en -->
<string name="main_saved_unsave_place_dialog_description">This place will be removed from your saved list.</string>
<!-- ja -->
<string name="main_saved_unsave_place_dialog_description">解除すると、このスポットが保存リストから消えます。</string>
```

### main_search_back

```xml
<!-- ko -->
<string name="main_search_back">뒤로</string>
<!-- en -->
<string name="main_search_back">Back</string>
<!-- ja -->
<string name="main_search_back">戻る</string>
```

### main_search_clear_all

```xml
<!-- ko -->
<string name="main_search_clear_all">전체 삭제</string>
<!-- en -->
<string name="main_search_clear_all">Clear all</string>
<!-- ja -->
<string name="main_search_clear_all">すべて削除</string>
```

### main_search_input_clear

```xml
<!-- ko -->
<string name="main_search_input_clear">검색어 지우기</string>
<!-- en -->
<string name="main_search_input_clear">Clear search</string>
<!-- ja -->
<string name="main_search_input_clear">検索語を消去</string>
```

### main_search_input_placeholder

```xml
<!-- ko -->
<string name="main_search_input_placeholder">코스 검색</string>
<!-- en -->
<string name="main_search_input_placeholder">Search courses</string>
<!-- ja -->
<string name="main_search_input_placeholder">コースを検索</string>
```

### main_search_placeholder

```xml
<!-- ko -->
<string name="main_search_placeholder">코스 검색</string>
<!-- en -->
<string name="main_search_placeholder">Search courses</string>
<!-- ja -->
<string name="main_search_placeholder">コース検索</string>
```

### main_search_placeholder_course

```xml
<!-- ko -->
<string name="main_search_placeholder_course">코스 검색</string>
<!-- en -->
<string name="main_search_placeholder_course">Search courses</string>
<!-- ja -->
<string name="main_search_placeholder_course">コースを検索</string>
```

### main_search_placeholder_place

```xml
<!-- ko -->
<string name="main_search_placeholder_place">장소 검색</string>
<!-- en -->
<string name="main_search_placeholder_place">Search places</string>
<!-- ja -->
<string name="main_search_placeholder_place">スポットを検索</string>
```

### main_search_places_empty

```xml
<!-- ko -->
<string name="main_search_places_empty">일치하는 장소가 없어요</string>
<!-- en -->
<string name="main_search_places_empty">No matching places</string>
<!-- ja -->
<string name="main_search_places_empty">一致する場所がありません</string>
```

### main_search_places_loading

```xml
<!-- ko -->
<string name="main_search_places_loading">장소를 찾고 있어요</string>
<!-- en -->
<string name="main_search_places_loading">Finding places…</string>
<!-- ja -->
<string name="main_search_places_loading">場所を検索中…</string>
```

### main_search_recent_title

```xml
<!-- ko -->
<string name="main_search_recent_title">최근 검색</string>
<!-- en -->
<string name="main_search_recent_title">Recent searches</string>
<!-- ja -->
<string name="main_search_recent_title">最近の検索</string>
```

### main_search_result_category_all

```xml
<!-- ko -->
<string name="main_search_result_category_all">전체</string>
<!-- en -->
<string name="main_search_result_category_all">All</string>
<!-- ja -->
<string name="main_search_result_category_all">すべて</string>
```

### main_search_result_chrome_back

```xml
<!-- ko -->
<string name="main_search_result_chrome_back">뒤로</string>
<!-- en -->
<string name="main_search_result_chrome_back">Back</string>
<!-- ja -->
<string name="main_search_result_chrome_back">戻る</string>
```

### main_search_result_chrome_clear_query

```xml
<!-- ko -->
<string name="main_search_result_chrome_clear_query">검색어 지우기</string>
<!-- en -->
<string name="main_search_result_chrome_clear_query">Clear search</string>
<!-- ja -->
<string name="main_search_result_chrome_clear_query">検索ワードを消す</string>
```

### main_search_result_chrome_map_open

```xml
<!-- ko -->
<string name="main_search_result_chrome_map_open">지도 보기</string>
<!-- en -->
<string name="main_search_result_chrome_map_open">View map</string>
<!-- ja -->
<string name="main_search_result_chrome_map_open">地図を見る</string>
```

### main_search_result_chrome_map_preview_place_count

```xml
<!-- ko -->
<string name="main_search_result_chrome_map_preview_place_count">장소 %1$d곳</string>
<!-- en -->
<string name="main_search_result_chrome_map_preview_place_count">%1$d places</string>
<!-- ja -->
<string name="main_search_result_chrome_map_preview_place_count">スポット%1$d件</string>
```

### main_search_result_chrome_map_preview_title

```xml
<!-- ko -->
<string name="main_search_result_chrome_map_preview_title">지도에서 한눈에 보기</string>
<!-- en -->
<string name="main_search_result_chrome_map_preview_title">See them all on the map</string>
<!-- ja -->
<string name="main_search_result_chrome_map_preview_title">地図でまとめて見る</string>
```

### main_search_result_empty_filtered

```xml
<!-- ko -->
<string name="main_search_result_empty_filtered">조건에 맞는 코스가 없어요. 필터를 조정해 보세요.</string>
<!-- en -->
<string name="main_search_result_empty_filtered">No courses match these filters. Try adjusting them.</string>
<!-- ja -->
<string name="main_search_result_empty_filtered">条件に合うコースがありません。フィルターを調整してください。</string>
```

### main_search_result_empty_query

```xml
<!-- ko -->
<string name="main_search_result_empty_query">\'%1$s\'에 맞는 코스가 없어요</string>
<!-- en -->
<string name="main_search_result_empty_query">No courses match \'%1$s\'</string>
<!-- ja -->
<string name="main_search_result_empty_query">\'%1$s\' に合うコースがありません</string>
```

### main_search_result_save_toggle_add

```xml
<!-- ko -->
<string name="main_search_result_save_toggle_add">저장</string>
<!-- en -->
<string name="main_search_result_save_toggle_add">Save</string>
<!-- ja -->
<string name="main_search_result_save_toggle_add">保存</string>
```

### main_search_result_save_toggle_remove

```xml
<!-- ko -->
<string name="main_search_result_save_toggle_remove">저장 해제</string>
<!-- en -->
<string name="main_search_result_save_toggle_remove">Remove from saved</string>
<!-- ja -->
<string name="main_search_result_save_toggle_remove">保存を解除</string>
```

### main_search_theme_find_title

```xml
<!-- ko -->
<string name="main_search_theme_find_title">테마로 찾기</string>
<!-- en -->
<string name="main_search_theme_find_title">Browse by theme</string>
<!-- ja -->
<string name="main_search_theme_find_title">テーマで探す</string>
```

### main_search_trending_title

```xml
<!-- ko -->
<string name="main_search_trending_title">지금 뜨는 검색어</string>
<!-- en -->
<string name="main_search_trending_title">Trending searches</string>
<!-- ja -->
<string name="main_search_trending_title">急上昇ワード</string>
```

### main_session_expired

```xml
<!-- ko -->
<string name="main_session_expired">세션이 만료되었어요. 다시 로그인해 주세요.</string>
<!-- en -->
<string name="main_session_expired">Your session has expired. Please log in again.</string>
<!-- ja -->
<string name="main_session_expired">セッションの有効期限が切れました。再度ログインしてください。</string>
```

### main_skeleton_courses_loading

```xml
<!-- ko -->
<string name="main_skeleton_courses_loading">코스를 불러오는 중</string>
<!-- en -->
<string name="main_skeleton_courses_loading">Loading courses</string>
<!-- ja -->
<string name="main_skeleton_courses_loading">コースを読み込み中</string>
```

### main_skeleton_follow_loading

```xml
<!-- ko -->
<string name="main_skeleton_follow_loading">팔로우 목록을 불러오는 중</string>
<!-- en -->
<string name="main_skeleton_follow_loading">Loading follow list</string>
<!-- ja -->
<string name="main_skeleton_follow_loading">フォローリストを読み込み中</string>
```

### main_skeleton_places_loading

```xml
<!-- ko -->
<string name="main_skeleton_places_loading">장소를 불러오는 중</string>
<!-- en -->
<string name="main_skeleton_places_loading">Loading places</string>
<!-- ja -->
<string name="main_skeleton_places_loading">場所を読み込み中</string>
```

### main_skeleton_profile_loading

```xml
<!-- ko -->
<string name="main_skeleton_profile_loading">프로필을 불러오는 중</string>
<!-- en -->
<string name="main_skeleton_profile_loading">Loading profile</string>
<!-- ja -->
<string name="main_skeleton_profile_loading">プロフィールを読み込み中</string>
```

### main_skeleton_recommended_loading

```xml
<!-- ko -->
<string name="main_skeleton_recommended_loading">추천 코스를 불러오는 중</string>
<!-- en -->
<string name="main_skeleton_recommended_loading">Loading recommended courses</string>
<!-- ja -->
<string name="main_skeleton_recommended_loading">おすすめコースを読み込み中</string>
```

### main_splash_offline_description

```xml
<!-- ko -->
<string name="main_splash_offline_description">Wi-Fi 나 데이터가 켜져 있는지 확인하고 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_splash_offline_description">Check that Wi-Fi or mobile data is on, then try again.</string>
<!-- ja -->
<string name="main_splash_offline_description">Wi-Fiまたはモバイルデータがオンになっているか確認して、もう一度お試しください。</string>
```

### main_splash_offline_dialog_description

```xml
<!-- ko -->
<string name="main_splash_offline_dialog_description">Wi-Fi 나 데이터가 켜져 있는지 확인하고 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_splash_offline_dialog_description">Check that Wi-Fi or mobile data is on, then try again.</string>
<!-- ja -->
<string name="main_splash_offline_dialog_description">Wi-Fiまたはモバイルデータがオンか確認して、もう一度お試しください。</string>
```

### main_splash_offline_dialog_retry

```xml
<!-- ko -->
<string name="main_splash_offline_dialog_retry">다시 시도</string>
<!-- en -->
<string name="main_splash_offline_dialog_retry">Retry</string>
<!-- ja -->
<string name="main_splash_offline_dialog_retry">再試行</string>
```

### main_splash_offline_dialog_title

```xml
<!-- ko -->
<string name="main_splash_offline_dialog_title">인터넷 연결이 필요해요</string>
<!-- en -->
<string name="main_splash_offline_dialog_title">You\'re offline</string>
<!-- ja -->
<string name="main_splash_offline_dialog_title">インターネット接続が必要です</string>
```

### main_splash_offline_retry

```xml
<!-- ko -->
<string name="main_splash_offline_retry">다시 시도</string>
<!-- en -->
<string name="main_splash_offline_retry">Retry</string>
<!-- ja -->
<string name="main_splash_offline_retry">再試行</string>
```

### main_splash_offline_title

```xml
<!-- ko -->
<string name="main_splash_offline_title">인터넷 연결이 필요해요</string>
<!-- en -->
<string name="main_splash_offline_title">You need an internet connection</string>
<!-- ja -->
<string name="main_splash_offline_title">インターネット接続が必要です</string>
```

### main_splash_subtitle

```xml
<!-- ko -->
<string name="main_splash_subtitle">나만의 코스, 나만의 동네</string>
<!-- en -->
<string name="main_splash_subtitle">My course, my neighborhood</string>
<!-- ja -->
<string name="main_splash_subtitle">わたしのコース、わたしの街</string>
```

### main_splash_tagline

```xml
<!-- ko -->
<string name="main_splash_tagline">나만의 코스, 나만의 동네</string>
<!-- en -->
<string name="main_splash_tagline">My courses, my neighborhood</string>
<!-- ja -->
<string name="main_splash_tagline">自分だけのコース、自分だけの街</string>
```

### main_terms_age14

```xml
<!-- ko -->
<string name="main_terms_age14">[필수] 만 14세 이상입니다</string>
<!-- en -->
<string name="main_terms_age14">[Required] I am 14 or older</string>
<!-- ja -->
<string name="main_terms_age14">[必須] 満14歳以上です</string>
```

### main_terms_age_14

```xml
<!-- ko -->
<string name="main_terms_age_14">[필수] 만 14세 이상입니다</string>
<!-- en -->
<string name="main_terms_age_14">[Required] I am 14 or older</string>
<!-- ja -->
<string name="main_terms_age_14">[必須] 満14歳以上です</string>
```

### main_terms_agree_all

```xml
<!-- ko -->
<string name="main_terms_agree_all">전체 동의</string>
<!-- en -->
<string name="main_terms_agree_all">Agree to all</string>
<!-- ja -->
<string name="main_terms_agree_all">すべて同意</string>
```

### main_terms_agree_continue

```xml
<!-- ko -->
<string name="main_terms_agree_continue">동의하고 계속하기</string>
<!-- en -->
<string name="main_terms_agree_continue">Agree and continue</string>
<!-- ja -->
<string name="main_terms_agree_continue">同意して続ける</string>
```

### main_terms_headline

```xml
<!-- ko -->
<string name="main_terms_headline">약관에\n동의해 주세요</string>
<!-- en -->
<string name="main_terms_headline">Please agree\nto the terms</string>
<!-- ja -->
<string name="main_terms_headline">利用規約に\n同意してください</string>
```

### main_terms_open_document_error

```xml
<!-- ko -->
<string name="main_terms_open_document_error">문서를 열 수 없어요. 잠시 후 다시 시도해 주세요.</string>
<!-- en -->
<string name="main_terms_open_document_error">Couldn\'t open the document. Please try again later.</string>
<!-- ja -->
<string name="main_terms_open_document_error">ドキュメントを開けませんでした。しばらくしてからもう一度お試しください。</string>
```

### main_terms_privacy_policy

```xml
<!-- ko -->
<string name="main_terms_privacy_policy">[필수] 개인정보 처리방침 동의</string>
<!-- en -->
<string name="main_terms_privacy_policy">[Required] Privacy Policy</string>
<!-- ja -->
<string name="main_terms_privacy_policy">[必須] プライバシーポリシーに同意</string>
```

### main_terms_service_terms

```xml
<!-- ko -->
<string name="main_terms_service_terms">[필수] 서비스 이용약관 동의</string>
<!-- en -->
<string name="main_terms_service_terms">[Required] Terms of Service</string>
<!-- ja -->
<string name="main_terms_service_terms">[必須] 利用規約に同意</string>
```

### main_terms_social_verified

```xml
<!-- ko -->
<string name="main_terms_social_verified">소셜 계정 확인 완료</string>
<!-- en -->
<string name="main_terms_social_verified">Social account verified</string>
<!-- ja -->
<string name="main_terms_social_verified">ソーシャルアカウント確認済み</string>
```

### main_terms_social_verified_badge

```xml
<!-- ko -->
<string name="main_terms_social_verified_badge">소셜 계정 확인 완료</string>
<!-- en -->
<string name="main_terms_social_verified_badge">Social account verified</string>
<!-- ja -->
<string name="main_terms_social_verified_badge">ソーシャルアカウント確認済み</string>
```

### main_terms_subtitle

```xml
<!-- ko -->
<string name="main_terms_subtitle">서비스 이용을 위해 아래 약관 동의가 필요해요.</string>
<!-- en -->
<string name="main_terms_subtitle">Agreeing to the terms below is required to use the service.</string>
<!-- ja -->
<string name="main_terms_subtitle">サービスのご利用には、以下の規約への同意が必要です。</string>
```

### main_terms_title

```xml
<!-- ko -->
<string name="main_terms_title">약관에\n동의해 주세요</string>
<!-- en -->
<string name="main_terms_title">Please agree\nto the terms</string>
<!-- ja -->
<string name="main_terms_title">規約に\n同意してください</string>
```

### main_terms_view_document

```xml
<!-- ko -->
<string name="main_terms_view_document">보기 ›</string>
<!-- en -->
<string name="main_terms_view_document">View ›</string>
<!-- ja -->
<string name="main_terms_view_document">表示 ›</string>
```

### main_trending_keyword_rank_down

```xml
<!-- ko -->
<string name="main_trending_keyword_rank_down">%1$d계단 하락</string>
<!-- en -->
<string name="main_trending_keyword_rank_down">Down %1$d places</string>
<!-- ja -->
<string name="main_trending_keyword_rank_down">%1$dランク下降</string>
```

### main_trending_keyword_rank_up

```xml
<!-- ko -->
<string name="main_trending_keyword_rank_up">%1$d계단 상승</string>
<!-- en -->
<string name="main_trending_keyword_rank_up">Up %1$d places</string>
<!-- ja -->
<string name="main_trending_keyword_rank_up">%1$dランク上昇</string>
```

### main_user_profile_share

```xml
<!-- ko -->
<string name="main_user_profile_share">공유</string>
<!-- en -->
<string name="main_user_profile_share">Share</string>
<!-- ja -->
<string name="main_user_profile_share">共有</string>
```

### settings_account

```xml
<!-- ko -->
<string name="settings_account">계정 관리</string>
<!-- en -->
<string name="settings_account">Account</string>
<!-- ja -->
<string name="settings_account">アカウント管理</string>
```

### settings_app_version

```xml
<!-- ko -->
<string name="settings_app_version">앱 버전</string>
<!-- en -->
<string name="settings_app_version">App version</string>
<!-- ja -->
<string name="settings_app_version">アプリバージョン</string>
```

### settings_contact

```xml
<!-- ko -->
<string name="settings_contact">문의하기</string>
<!-- en -->
<string name="settings_contact">Contact us</string>
<!-- ja -->
<string name="settings_contact">お問い合わせ</string>
```

### settings_hint_not_ready

```xml
<!-- ko -->
<string name="settings_hint_not_ready">(준비 중)</string>
<!-- en -->
<string name="settings_hint_not_ready">(Coming soon)</string>
<!-- ja -->
<string name="settings_hint_not_ready">（準備中）</string>
```

### settings_language

```xml
<!-- ko -->
<string name="settings_language">언어</string>
<!-- en -->
<string name="settings_language">Language</string>
<!-- ja -->
<string name="settings_language">言語</string>
```

### settings_notice

```xml
<!-- ko -->
<string name="settings_notice">공지</string>
<!-- en -->
<string name="settings_notice">Notices</string>
<!-- ja -->
<string name="settings_notice">お知らせ</string>
```

### settings_notification

```xml
<!-- ko -->
<string name="settings_notification">알림</string>
<!-- en -->
<string name="settings_notification">Notifications</string>
<!-- ja -->
<string name="settings_notification">通知</string>
```

### settings_privacy

```xml
<!-- ko -->
<string name="settings_privacy">개인정보 보호</string>
<!-- en -->
<string name="settings_privacy">Privacy</string>
<!-- ja -->
<string name="settings_privacy">プライバシー</string>
```

### settings_profile_edit

```xml
<!-- ko -->
<string name="settings_profile_edit">프로필 수정</string>
<!-- en -->
<string name="settings_profile_edit">Edit profile</string>
<!-- ja -->
<string name="settings_profile_edit">プロフィール編集</string>
```

### settings_title

```xml
<!-- ko -->
<string name="settings_title">설정</string>
<!-- en -->
<string name="settings_title">Settings</string>
<!-- ja -->
<string name="settings_title">設定</string>
```

### settings_toast_no_browser

```xml
<!-- ko -->
<string name="settings_toast_no_browser">브라우저를 열 수 없어요</string>
<!-- en -->
<string name="settings_toast_no_browser">Can\'t open a browser</string>
<!-- ja -->
<string name="settings_toast_no_browser">ブラウザを開けません</string>
```

### settings_toast_not_ready

```xml
<!-- ko -->
<string name="settings_toast_not_ready">준비 중이에요</string>
<!-- en -->
<string name="settings_toast_not_ready">Coming soon</string>
<!-- ja -->
<string name="settings_toast_not_ready">準備中です</string>
```

## #180 통합 추가: main/presentation/src/main/res/values/strings.xml

### main_profile_edit_loading

```xml
<!-- ko -->
<string name="main_profile_edit_loading">프로필을 확인하고 있어요</string>
<!-- en -->
<string name="main_profile_edit_loading">Loading your profile</string>
<!-- ja -->
<string name="main_profile_edit_loading">プロフィールを確認しています</string>
```
