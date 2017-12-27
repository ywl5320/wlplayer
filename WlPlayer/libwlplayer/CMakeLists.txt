
cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/include)

add_library( # Sets the name of the library.
             wlplayer

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/wlplayer.cpp
             src/main/cpp/WlListener.cpp
             src/main/cpp/WlFFmpeg.cpp
             src/main/cpp/WlBasePlayer.cpp
             src/main/cpp/WlAudio.cpp
             src/main/cpp/WlVideo.cpp
             src/main/cpp/WlQueue.cpp
             src/main/cpp/WlOpenGLES.cpp
             src/main/cpp/WlOpenSLES.cpp
             src/main/cpp/WlPlayStatus.cpp)


#添加libavcodec-57.so
add_library( avcodec-57
             SHARED
             IMPORTED)
set_target_properties( avcodec-57
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavcodec-57.so)


#添加libavdevice-57.so
add_library( avdevice-57
             SHARED
             IMPORTED)
set_target_properties( avdevice-57
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavdevice-57.so)

add_library( avfilter-6
             SHARED
             IMPORTED)
set_target_properties( avfilter-6
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavfilter-6.so)

add_library( avformat-57
             SHARED
             IMPORTED)
set_target_properties( avformat-57
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavformat-57.so)

add_library( avutil-55
             SHARED
             IMPORTED)
set_target_properties( avutil-55
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libavutil-55.so)

add_library( swresample-2
             SHARED
             IMPORTED)
set_target_properties( swresample-2
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswresample-2.so)

add_library( swscale-4
             SHARED
             IMPORTED)
set_target_properties( swscale-4
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libswscale-4.so)

add_library( postproc-54
             SHARED
             IMPORTED)
set_target_properties( postproc-54
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi/libpostproc-54.so)



target_link_libraries( # Specifies the target library.
                       wlplayer
                       avcodec-57
                       avdevice-57
                       avfilter-6
                       avformat-57
                       avutil-55
                       swresample-2
                       swscale-4
                       postproc-54
                       OpenSLES
                       GLESv2
                       android
                       log
                        )