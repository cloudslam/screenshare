#pragma once

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

bool rpmsg_startClient();
bool rpmsg_block_send(const char* message);
void rpmsg_stopClient();

#ifdef __cplusplus
}
#endif
