# Copyright (C) 2019, Fuzhou Rockchip Electronics Co., Ltd
# Copyright (C) 2025, HoloIndustry
# Released under the MIT license (see COPYING.MIT for the terms)
#
# Mainline U-Boot for Rockchip RK3588 with rkbin firmware blobs.
# Replaces BSP U-Boot 2017.09 — enables standard boot.scr, persistent
# environment, and proper RAUC A/B OTA with bootloader-level rollback.

require recipes-bsp/u-boot/u-boot.inc
require recipes-bsp/u-boot/u-boot-common.inc

PROVIDES = "virtual/bootloader"

PV = "2025.04"

LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

# Mainline U-Boot v2025.04
SRCREV = "9f8c7a44c3812e4c781b5ce9a0400eebc218136e"
SRC_URI = "git://source.denx.de/u-boot/u-boot.git;protocol=https;branch=master"

# Rockchip binary firmware (DDR training blob + ARM Trusted Firmware)
SRCREV_rkbin = "74213af1e952c4683d2e35952507133b61394862"
SRC_URI += "git://github.com/rockchip-linux/rkbin.git;protocol=https;branch=master;name=rkbin;destsuffix=rkbin"
SRCREV_FORMAT = "default_rkbin"

DEPENDS += "bc-native dtc-native python3-pyelftools-native"

# Rockchip firmware paths (exported as env vars for the U-Boot build)
RK_BL31 = "${WORKDIR}/rkbin/bin/rk35/rk3588_bl31_v1.51.elf"
RK_TPL = "${WORKDIR}/rkbin/bin/rk35/rk3588_ddr_lp4_2112MHz_lp5_2400MHz_v1.19.bin"

# Mainline U-Boot build produces these
UBOOT_BINARY = "u-boot.itb"

do_configure:prepend() {
    # Mainline U-Boot needs BL31 and TPL available during configure/compile
    export BL31="${RK_BL31}"
    export ROCKCHIP_TPL="${RK_TPL}"
}

do_compile:prepend() {
    export BL31="${RK_BL31}"
    export ROCKCHIP_TPL="${RK_TPL}"
}

do_compile:append() {
    # Verify expected outputs exist
    for f in idbloader.img u-boot.itb u-boot-rockchip.bin; do
        if [ ! -f "${B}/${f}" ]; then
            bbfatal "${PN}: Expected output ${f} not found after build"
        fi
    done
}

do_deploy:append() {
    # Mainline U-Boot artifacts
    install -m 0644 ${B}/idbloader.img ${DEPLOYDIR}/idbloader.img
    install -m 0644 ${B}/u-boot.itb ${DEPLOYDIR}/u-boot.itb
    install -m 0644 ${B}/u-boot-rockchip.bin ${DEPLOYDIR}/u-boot-rockchip.bin

    # Backward-compatible symlinks for WKS / rockchip-image.bbclass
    ln -sf idbloader.img ${DEPLOYDIR}/idblock.img
}
