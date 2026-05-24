#include "seRpmsg.h"

#include <android-base/logging.h>

bool rpmsg_startClient() {
    LOG(INFO) << "rpmsg_startClient: initialize rpmsg client";
    return true;
}

bool rpmsg_block_send(const char* message) {
    LOG(INFO) << "rpmsg_block_send: " << (message == nullptr ? "null" : message);
    return true;
}

void rpmsg_stopClient() {
    LOG(INFO) << "rpmsg_stopClient: release rpmsg client";
}
