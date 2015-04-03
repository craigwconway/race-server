package com.bibsmobile.util;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.bibsmobile.controller.EventController;
import com.bibsmobile.model.AwardCategoryResults;
import com.bibsmobile.model.Event;
import com.bibsmobile.model.RaceResult;

public class PDFUtil {
	public static PDDocument createColorMedalsPDF(Event event, List<AwardCategoryResults> list) {
		PDDocument document = new PDDocument();
		try {
			float yPos = 700;
			boolean started = false;
			PDPageContentStream contentStream = null;
			for(AwardCategoryResults ac: list) {
				if(!ac.getResults().isEmpty()) {
					//WE GOTTA RENDER A NEW PAGE YO
					if(yPos - (ac.getResults().size()*22) < 130 || !started) {
						if(started) {
							contentStream.close();
						}
						System.out.println("ahhh");
						PDPage page = new PDPage();
						document.addPage(page);
						started = true;
						contentStream = new PDPageContentStream(document,page);
						contentStream.beginText();
						contentStream.setFont(PDType1Font.HELVETICA,8);
						contentStream.moveTextPositionByAmount(10, 10);
						contentStream.drawString("Results for " +event.getName() + "  -- powered by bibs");
						contentStream.endText();
						yPos=700;
					}
					
					contentStream.beginText();
					contentStream.setFont(PDType1Font.HELVETICA, 20);
					contentStream.moveTextPositionByAmount(100, yPos);
					contentStream.drawString(ac.getCategory().getName());
					contentStream.endText();
					contentStream.addLine(95, yPos-(22*ac.getResults().size()), 95, yPos-18);
					contentStream.addBezier32(95, yPos-11, 105, yPos-7);
					contentStream.addLine(105, yPos-7, 360, yPos-7);
					contentStream.setStrokingColor(225);
					contentStream.stroke();
					for(RaceResult rr : ac.getResults()) {
						yPos -=22;
						contentStream.beginText();
						contentStream.setFont(PDType1Font.HELVETICA, 12);
						contentStream.moveTextPositionByAmount(100, yPos);
						contentStream.drawString(
						rr.getTimeofficialdisplay() + "\t\t"
						+ rr.getAge() + "\t\t"
						+ rr.getFirstname() + " "
						+ rr.getLastname());
						contentStream.endText();
					}
					yPos -=34;
				}
			}
			contentStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;
	}
}
