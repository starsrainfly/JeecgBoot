package org.jeecg.modules.mes.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.mdm.entity.LabelTemplate;
import org.jeecg.modules.mdm.service.ILabelTemplateService;
import org.jeecg.modules.mes.entity.LabelPrintTask;
import org.jeecg.modules.mes.mapper.LabelPrintTaskMapper;
import org.jeecg.modules.mes.service.ILabelPrintTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: mis_label_print_task
 * @Author: jeecg-boot
 * @Date:   2026-04-27
 * @Version: V1.0
 */
@Slf4j
@Service
public class LabelPrintTaskServiceImpl extends ServiceImpl<LabelPrintTaskMapper, LabelPrintTask> implements ILabelPrintTaskService {

    @Autowired
    private ILabelTemplateService labelTemplateService;

    @Value("${jeecg.path.upload}")
    private String uploadPath;

    @Override
    public String generateLabelImage(String taskId) {
        return generateLabelImage(taskId, 300);
    }

    @Override
    public String generateLabelImage(String taskId, int dpi) {
        LabelPrintTask task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("打印任务不存在");
        }

        // 查模板
        LabelTemplate template = null;
        if (StringUtils.isNotBlank(task.getTemplateId())) {
            template = labelTemplateService.getById(task.getTemplateId());
        }

        BufferedImage image = doGenerateImage(task, template, dpi);

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", os);
            byte[] bytes = os.toByteArray();
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

            // 打印长度检查
            log.info("生成标签图片 Base64 长度: {}", base64.length());
            log.info("Base64 前100字符: {}", base64.substring(0, Math.min(100, base64.length())));

            return base64;
        } catch (IOException e) {
            log.error("标签图片生成失败", e);
            throw new RuntimeException("标签图片生成失败");
        }
    }

    /**
     * 核心绘制方法
     */
    private BufferedImage doGenerateImage(LabelPrintTask task, LabelTemplate template, int dpi) {
        // 1mm = dpi/25.4 px
        double mmToPx = dpi / 25.4;

        // 尺寸优先用模板，模板没有再用任务自身
        int labelWidth = (template != null && template.getLabelWidth() != null)
                ? template.getLabelWidth() : task.getLabelWidth();
        int labelHeight = (template != null && template.getLabelHeight() != null)
                ? template.getLabelHeight() : task.getLabelHeight();

        int widthPx = (int) Math.round(labelWidth * mmToPx);
        int heightPx = (int) Math.round(labelHeight * mmToPx);

        log.info("生成标签: {}x{}mm, {}dpi, {}x{}px", labelWidth, labelHeight, dpi, widthPx, heightPx);

        BufferedImage image = new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 关闭抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, widthPx, heightPx);

        // 黑色边框
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, widthPx - 1, heightPx - 1);

        // 解析模板元素 - 从 LabelTemplate.contentJson 取
        String templateJson = null;
        if (template != null && StringUtils.isNotBlank(template.getContentJson())) {
            templateJson = template.getContentJson();
        }

        if (StringUtils.isBlank(templateJson)) {
            templateJson = getDefaultTemplateJson(labelWidth, labelHeight);
        }

        JSONObject templateObj = JSON.parseObject(templateJson);
        JSONArray elements = templateObj.getJSONArray("elements");

        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                JSONObject el = elements.getJSONObject(i);
                try {
                    drawElement(g2d, el, task, mmToPx);
                } catch (Exception e) {
                    log.error("绘制元素失败: field={}, error={}", el.getString("field"), e.getMessage());
                }
            }
        }

        g2d.dispose();
        return image;
    }

    /**
     * 绘制单个元素
     */
    private void drawElement(Graphics2D g2d, JSONObject el, LabelPrintTask task, double mmToPx) throws Exception {
        String type = el.getString("type");
        String field = el.getString("field");
        double x = el.getDoubleValue("x");
        double y = el.getDoubleValue("y");

        int px = (int) Math.round(x * mmToPx);
        int py = (int) Math.round(y * mmToPx);

        String value = getElementValue(el, task);
        if (StringUtils.isBlank(value) && !"datePrefix".equals(field)) {
            return;
        }

        switch (type) {
            case "text":
                drawText(g2d, el, px, py, value, mmToPx);
                break;
            case "barcode":
                drawBarcode(g2d, el, px, py, value, mmToPx);
                break;
            case "qrcode":
                drawQrCode(g2d, el, px, py, task, mmToPx);
                break;
        }
    }

    /**
     * 绘制文字
     */
    private void drawText(Graphics2D g2d, JSONObject el, int x, int y,
                          String value, double mmToPx) {
        float fontSizePt = el.getFloatValue("fontSize");
        if (fontSizePt <= 0) fontSizePt = 9f;

        // pt 转 px: 1pt ≈ 0.3528mm
        int fontSizePx = (int) Math.round(fontSizePt * 0.3528 * mmToPx);

        boolean bold = el.getBooleanValue("bold");
        Font font = new Font("Microsoft YaHei", bold ? Font.BOLD : Font.PLAIN, Math.max(fontSizePx, 8));
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        // 计算文字尺寸
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(value, frc);
        int textWidth = (int) bounds.getWidth();
        int textHeight = (int) bounds.getHeight();

        // 对齐
        String align = el.getString("align");
        double width = el.getDoubleValue("width");
        int drawX = x;

        if ("center".equals(align)) {
            int areaWidth = (int) Math.round(width * mmToPx);
            drawX = x + (areaWidth - textWidth) / 2;
        } else if ("right".equals(align)) {
            int areaWidth = (int) Math.round(width * mmToPx);
            drawX = x + areaWidth - textWidth;
        }

        int drawY = y + textHeight;
        g2d.drawString(value, drawX, drawY);
    }

    /**
     * 绘制条码 - 用 zxing CODE_128 替代 barcode4j
     */
    private void drawBarcode(Graphics2D g2d, JSONObject el, int x, int y,
                             String value, double mmToPx) throws Exception {
        double width = el.getDoubleValue("width");
        double height = el.getDoubleValue("height");

        int widthPx = (int) Math.round(width * mmToPx);
        int heightPx = (int) Math.round(height * mmToPx);

        // 条码条占 70% 高度，文字区占 30%
        int barHeightPx = (int) Math.round(heightPx * 0.70);
        int textAreaPx = heightPx - barHeightPx;

        // 用 zxing 生成 CODE_128 条码，去除留白
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(value, BarcodeFormat.CODE_128, widthPx, barHeightPx, hints);
        BufferedImage barcodeImg = MatrixToImageWriter.toBufferedImage(matrix);

        // 绘制条码条
        g2d.drawImage(barcodeImg, x, y, widthPx, barHeightPx, null);

        // 绘制文字 - 按比例计算字号
        String displayValue = value;

        // 文字高度占条码总高度的 22%，最小 8px，最大不超过文字区域 80%
        int fontSizePx = Math.max((int) (heightPx * 0.22), 8);
        fontSizePx = Math.min(fontSizePx, (int) (textAreaPx * 0.8));

        Font font = new Font("Arial", Font.PLAIN, fontSizePx);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D textBounds = font.getStringBounds(displayValue, frc);
        int textWidth = (int) textBounds.getWidth();
        int textHeight = (int) textBounds.getHeight();

        int textX = x + (widthPx - textWidth) / 2;
        int textY = y + barHeightPx + (textAreaPx - textHeight) / 2 + textHeight;
        g2d.drawString(displayValue, textX, textY);
    }

    /**
     * 绘制二维码
     */
    private void drawQrCode(Graphics2D g2d, JSONObject el, int x, int y,
                            LabelPrintTask task, double mmToPx) throws Exception {
        double size = el.getDoubleValue("size");
        int sizePx = (int) Math.round(size * mmToPx);

        String value = task.getQrContent();
        if (StringUtils.isBlank(value)) return;

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 0);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(value, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);
        BufferedImage qrImg = MatrixToImageWriter.toBufferedImage(matrix);

        g2d.drawImage(qrImg, x, y, sizePx, sizePx, null);
    }

    /**
     * 获取元素值
     */
    private String getElementValue(JSONObject el, LabelPrintTask task) {
        String field = el.getString("field");
        String fixedValue = el.getString("value");

        if (StringUtils.isNotBlank(fixedValue)) {
            return fixedValue;
        }

        switch (field) {
            case "companyName":
                return StringUtils.defaultString(task.getCompanyName(), "");
            case "productName":
                return StringUtils.defaultString(
                        StringUtils.isNotBlank(task.getPrintProductName()) ? task.getPrintProductName() : task.getProductName(),
                        "");
            case "productCode":
                return StringUtils.defaultString(task.getProductCode(), "");
            case "color":
                return StringUtils.defaultString(task.getProductColor(), "");
            case "batchNo":
                return StringUtils.defaultString(task.getBatchNo(), "");
            case "produceDate":
                return StringUtils.defaultString(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
            case "datePrefix":
                return "日期:";
            case "qcStatus":
                return StringUtils.defaultString("合格");
            case "qrCode":
                return StringUtils.defaultString(task.getQrContent(), "");
            default:
                return "";
        }
    }

    /**
     * 保存图片
     */
    private String saveImage(BufferedImage image, String taskNo) {
        String fileName = "label_" + taskNo + "_" + System.currentTimeMillis() + ".png";
        String relativePath = "temp/label/" + fileName;
        String fullPath = uploadPath + File.separator + relativePath;

        File dir = new File(fullPath).getParentFile();
        if (!dir.exists()) dir.mkdirs();

        try {
            ImageIO.write(image, "PNG", new File(fullPath));
        } catch (IOException e) {
            log.error("保存图片失败", e);
            throw new RuntimeException("保存图片失败");
        }

        return fullPath;
    }

    /**
     * 默认模板
     */
    private String getDefaultTemplateJson(int width, int height) {
        return "{" +
                "\"version\":\"1.0\"," +
                "\"page\":{\"width\":" + width + ",\"height\":" + height + ",\"dpi\":300}," +
                "\"elements\":[" +
                "{\"type\":\"text\",\"field\":\"companyName\",\"label\":\"公司名称\",\"x\":2,\"y\":1,\"fontSize\":7,\"align\":\"center\",\"width\":56}," +
                "{\"type\":\"text\",\"field\":\"productName\",\"label\":\"产品名称\",\"x\":2,\"y\":6,\"fontSize\":11,\"bold\":true,\"width\":28}," +
                "{\"type\":\"text\",\"field\":\"color\",\"label\":\"颜色\",\"x\":2,\"y\":12,\"fontSize\":8,\"width\":28}," +
                "{\"type\":\"barcode\",\"field\":\"batchNo\",\"label\":\"批次条码\",\"x\":2,\"y\":14,\"width\":32,\"height\":16,\"format\":\"CODE128\"}," +
                "{\"type\":\"text\",\"field\":\"datePrefix\",\"label\":\"日期前缀\",\"x\":2,\"y\":28,\"fontSize\":7,\"value\":\"日期:\",\"width\":28}," +
                "{\"type\":\"text\",\"field\":\"produceDate\",\"label\":\"生产日期\",\"x\":8,\"y\":28,\"fontSize\":7,\"width\":28}," +
                "{\"type\":\"qrcode\",\"field\":\"qrCode\",\"label\":\"二维码\",\"x\":35,\"y\":4,\"size\":22}," +
                "{\"type\":\"text\",\"field\":\"qcStatus\",\"label\":\"质检状态\",\"x\":38,\"y\":27,\"fontSize\":10,\"bold\":true,\"width\":20}" +
                "]" +
                "}";
    }
}
