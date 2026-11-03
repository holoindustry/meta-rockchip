# Copyright (C) 2024, Rockchip Electronics Co., Ltd
# Released under the MIT license (see COPYING.MIT for the terms)

require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

inherit auto-patch

inherit local-git

SRCREV = "77168c8d5ab82399f65a80e9f807b50ba37cf483"
SRC_URI = " \
	git://github.com/rockchip-linux/kernel.git;protocol=https;nobranch=1;branch=develop-6.1; \
"

S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
