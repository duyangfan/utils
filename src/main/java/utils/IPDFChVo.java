package utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.BaseFont;

public interface IPDFChVo {

    public void setContent(BaseFont bfChinese, Document document)throws Exception;
}