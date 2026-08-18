# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# 디버깅 편의를 위해 라인 번호 보존. (ANR/크래시 리포트에서 의미 있는 스택트레이스 확보)
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

##---------------- kotlinx.serialization ----------------
# 본 프로젝트는 DTO / Navigation3 의 GenericNavKey / NavRoute 직렬화 등에 광범위하게
# kotlinx.serialization 을 사용한다. 직렬화 코드는 컴파일 시 생성된 *$Companion.serializer()
# 또는 object 의 INSTANCE.serializer() 를 리플렉션으로 호출하기 때문에 아래 규칙들이 없으면
# release 빌드에서 SerializationException 이 발생한다.

# @Serializable / @Polymorphic 등 런타임 어노테이션 보존.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# @Serializable 표시된 클래스의 Companion 필드 보존.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Companion 객체의 serializer() 메서드 보존.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable object 의 INSTANCE.serializer() 보존.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# 컴파일러가 생성하는 $$serializer 내부 클래스 자체와 그 INSTANCE 필드 보존.
-keepclasseswithmembers class **$$serializer {
    *** INSTANCE;
}

##---------------- Kakao SDK ----------------
# Kakao SDK 는 에러 객체를 만들 때 enum 상수를 이름으로 리플렉션 조회한다.
# KakaoClientError.kt 의 ClientError 보조 생성자가 실제로 하는 일:
#
#     reason.javaClass.getField(reason.name).getAnnotation(Description::class.java)?.value
#         ?: "Client-side error"
#
# R8 이 ClientErrorCause.TokenNotFound 를 q 로 바꾸면 getField("TokenNotFound") 가
# NoSuchFieldException 을 던진다. 이건 IOException 이 아니라서 OkHttp AsyncCall.run 의
# `catch (t: Throwable) { ...; throw t }` 를 타고 워커 스레드 밖으로 튀어나가 프로세스가 죽는다.
# (release 에서 로그인 취소·토큰 없음 등 모든 ClientError 경로가 즉시 크래시)
#
# v2-common AAR 에는 consumer proguard 규칙이 아예 없어서 앱이 직접 지정해야 한다.
# 필드 이름 보존은 Gson 기반 모델 역직렬화에도 함께 필요하다.
-keep class com.kakao.sdk.**.model.* { <fields>; }

# Kakao SDK 가 내부적으로 등록하는 Gson TypeAdapter 들.
-keep class * extends com.google.gson.TypeAdapter
