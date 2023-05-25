package com.godq.cms.update

import timber.log.Timber
import java.lang.Exception
import java.util.*
import java.util.regex.Pattern

fun formatBillInfoFromClipBoard(clipboardText: String): BillInfo? {

    return try {

        if (!clipboardText.contains("营业额")) {
            return null
        }

        val info = BillInfo()

        //店铺
        val shopPattern = Pattern.compile("##.*##")
        shopPattern.matcher(clipboardText).apply {
            if (find()) {
                when (group().trim().replace("#", "")) {
                    "乐松" -> {
                        info.shopId = "2"
                    }
                    else -> {
                        info.shopId = "1"
                    }
                }
            }
        }


        //日期
        val datePattern = Pattern.compile("(\\d+)\\s*月(\\d+)\\s*[号日]\\s*(?=营业额报表)")
        datePattern.matcher(clipboardText).apply {
            if (find()) {
                val year = Calendar.getInstance().get(Calendar.YEAR)
                val date = String.format("%d-%02d-%02d", year, group(1)?.trim()?.toInt() ?: 0, group(2)?.trim()?.toInt() ?: 0)
                info.date = date
            }
        }

        //桌次
        val tableTimesPattern = Pattern.compile("(?<=桌数\\s{0,10}[:：])\\s*[\\d.,，。\\s]+\\s*(?=桌)")
        tableTimesPattern.matcher(clipboardText).apply {
            if (find()) {
                info.tableTimes = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //银行卡
        val bankPattern = Pattern.compile("(?<=银行卡\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        bankPattern.matcher(clipboardText).apply {
            if (find()) {
                info.bankAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //支付宝
        val aliPattern = Pattern.compile("(?<=支付宝\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        aliPattern.matcher(clipboardText).apply {
            if (find()) {
                info.aliAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //微信
        val wxPattern = Pattern.compile("(?<=微信\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        wxPattern.matcher(clipboardText).apply {
            if (find()) {
                info.wxAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //现金
        val cashPattern = Pattern.compile("(?<=现金\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        cashPattern.matcher(clipboardText).apply {
            if (find()) {
                info.cashAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //美团
        val mtPattern = Pattern.compile("(?<=美团\\s{0,10}[:：]\\s{0,10}[\\d.,，。\\s]{1,30}张)[\\d.,，。\\s]+(?=[元块圆])")
        mtPattern.matcher(clipboardText).apply {
            if (find()) {
                info.meituanAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //抖音 "抖音:1  ,1.2，4。4张1  ,1.2，4。4元\n"
        val dyPattern = Pattern.compile("(?<=抖音\\s{0,10}[:：]\\s{0,10}[\\d.,，。\\s]{0,30}张)[\\d.,，。\\s]+(?=[元块圆])")
        dyPattern.matcher(clipboardText).apply {
            if (find()) {
                info.douyinAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //外卖 "外卖:1  ,1.2，4。4单1  ,1.2，4。4元\n"
        val waimaiPattern = Pattern.compile("(?<=外卖\\s{0,10}[:：]\\s{0,10}[\\d.,，。\\s]{1,30}单)[\\d.,，。\\s]+(?=[元块圆])")
        waimaiPattern.matcher(clipboardText).apply {
            if (find()) {
                info.waimaiAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //外卖 "外卖:1  ,1.2，4。4单1  ,1.2，4。4元\n"
        val otherPattern = Pattern.compile("(?<=其他\\s{0,10}[:：]\\s{0,10}[\\d.,，。\\s]{1,30}单)[\\d.,，。\\s]+(?=[元块圆])")
        otherPattern.matcher(clipboardText).apply {
            if (find()) {
                info.waimaiAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //免单
        val freePattern = Pattern.compile("(?<=免单\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        freePattern.matcher(clipboardText).apply {
            if (find()) {
                info.freeAmount = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //支出
        val payOutPattern = Pattern.compile("(?<=支出\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOut = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //食材
        val payOutMaterialsPattern = Pattern.compile("(?<=食材\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutMaterialsPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutMaterials = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //人工
        val payOutLaborPattern = Pattern.compile("(?<=人工\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutLaborPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutLabor = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //水费
        val payOutWaterPattern = Pattern.compile("(?<=水费\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutWaterPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutWater = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //电费
        val payOutElectricityPattern = Pattern.compile("(?<=电费\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutElectricityPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutElectricity = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //燃气
        val payOutGasPattern = Pattern.compile("(?<=燃气\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutGasPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutGas = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //分红
        val payOutDividendsPattern = Pattern.compile("(?<=分红\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutDividendsPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutDividends = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //其他支出
        val payOutOtherPattern = Pattern.compile("(?<=其他支出\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        payOutOtherPattern.matcher(clipboardText).apply {
            if (find()) {
                info.payOutOther = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }

        //总流水
        val totalPattern = Pattern.compile("(?<=营业额\\s{0,10}[:：])[\\d.,，。\\s]+(?=[元块圆])")
        totalPattern.matcher(clipboardText).apply {
            if (find()) {
                info.total = group().trim().replace("[,，。\\s]+".toRegex(), "")
            }
        }


        Timber.tag("format").e(info.toString())
        info
    } catch (e:Exception) {
        null
    }

}