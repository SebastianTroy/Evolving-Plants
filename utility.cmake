FetchContent_Declare(
    utility
    GIT_REPOSITORY  https://github.com/SebastianTroy/Utility
    GIT_TAG         origin/main
)

FetchContent_MakeAvailable(utility)

include_directories(
    "${utility_SOURCE_DIR}"
)
