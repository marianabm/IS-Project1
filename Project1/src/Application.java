import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import generated.Citation;
import generated.CitationInformation;
import generated.Citations;
import generated.NumberOfCitations;
import generated.Publication;
import generated.Publications;
import generated.Researcher;

/**
 * 
 * @author marianabm
 * @author Mariana Bianchi Marques
 * @author Mikely Fernanda Saraiva da Silva
 *
 */
public class Application {
	
	/**
	 * Main application method
	 * @param args
	 */
	public static void main(String args[]){
		
		Researcher researcher;
		String fileName = "file.xml";
		String xslUrl = "formattedHTML.xsl";
		String url = "https://eden.dei.uc.pt/~cnl/scholar/index.html";
		String[] authorsIgnored = null, titlesIgnored = null, years = null;
		int[] rangeOfYears = new int[2];
		Scanner input = new Scanner(System.in);		
			        
        //ignore citations from certain authors
        System.out.println("Ignore citations from the authors: (Separate the names with '/') ");
        authorsIgnored = input.nextLine().split("/");
                
        //ignore citations that matching certain titles
        System.out.println("Ignore citations that matching the titles: (Separate the titles with '/')");
        titlesIgnored = input.nextLine().split("/");
        		
        //present citations from a given range of years
        System.out.println("What should be the citations' range of years? (Separate the years with '-')");
        years = input.nextLine().split("-");
        if(years.length == 2 && (Integer.parseInt(years[0]) < Integer.parseInt(years[1]))){
        	rangeOfYears[0] = Integer.parseInt(years[0]);
        	rangeOfYears[1] = Integer.parseInt(years[1]);
        }
        else{
        	rangeOfYears[0] = 1800;
        	rangeOfYears[1] = 2017;
        }
                
		researcher = retrieveInformation(url,authorsIgnored, titlesIgnored, rangeOfYears);
		generateXml(researcher, fileName, xslUrl);
		System.out.println("O arquivo XML foi gerado. ");
		
	}
	
	/**
	 * Retrieve informations about the researcher profile
	 * @param url link to the researcher profile
	 * @param authorsIgnored stores the names of the authors whose the citations will be ignored
	 * @param titlesIgnored stores the titles of articles whose citations will be ignored
	 * @param rangeOfYears stores the range of years by filtering the citations for years
	 * @return Returns an object representing the XML file of the researcher 
	 */
	private static Researcher retrieveInformation(String url, String[] authorsIgnored, String[] titlesIgnored, int[] rangeOfYears){
		
		Document doc;
		Researcher researcher = new Researcher();
		Citations citations = new Citations();
		Publications publications = new Publications();
		Map<Integer,Integer> citationsPerYear = new TreeMap<Integer, Integer>();				
		int totalCitations = 0;
								
		try {		
            
			//avoid timeout error
			Jsoup.connect(url).timeout(7000);
			
			//Visiting the link to the researcher profile
			doc = Jsoup.connect(url).get();
			
			//Getting the name 
			Elements name = doc.select("div#gsc_prf_in");
			String nameResearcher = name.text();
			researcher.setName(nameResearcher);
								
			//Publications
			
			//Obtaining the set of links referring to each of the publications of the researcher
			Elements newsHeadlines = doc.select("a[href].gsc_a_at");
							
			
			//Getting informations about each publication
			Iterator<Element> iterator = newsHeadlines.iterator();
			while(iterator.hasNext()){
							
				Element element = iterator.next();
				Iterator<Element> iteratorField, iteratorValue;
				Publication publication = new Publication();
				CitationInformation citationInformation = new CitationInformation();
				String publi = element.attr("href");
				doc = Jsoup.connect("https://eden.dei.uc.pt/~cnl/scholar/" + publi).get();
				Elements citedBy = doc.select("a:contains(Cited)");
				String linkCited = citedBy.attr("href");
				String additionalInformation = "";
								
				//Verifying whether the publication has citations
				if(linkCited != ""){				
					
					String title = element.text();
					String[] urlCitations;
					publication.setTitle(title);
					List<Citation> citationList;
					Elements nextPage, citationsLink;
					
					Elements fields = doc.select("div.gsc_field");
					Elements values = doc.select("div.gsc_value");
					
					iteratorField = fields.iterator();
					iteratorValue = values.iterator();					
					//Getting informations about the publication
					while(iteratorField.hasNext()){
						
						Element field = iteratorField.next();
						Element value = iteratorValue.next();
						
						if(field.text().equals("Authors")){
							String[] authors = value.text().split(", ");
							for(int k=0; k<authors.length; k++){
								publication.getAuthor().add(authors[k]);
							}							
						}						
						else if(field.text().equals("Publication date")){
							publication.setDate(value.text());							
						}
						else if(field.text().equals("Publisher")){
							publication.setPublisher(value.text());
						}
						else if(field.text().equals("Description")){
							publication.setDescription(value.text());
						}						
						else if(!field.text().equals("Total citations") && !field.text().equals("Scholar articles")){
							additionalInformation = additionalInformation + field.text() 
									+ ": " + value.text() + "\n";							
						}					
					}//while(iteratorField.hasNext())					
					
					publication.setAdditionalInformation(additionalInformation);
															
					//Obtaining the set of citations from the publication
					doc = Jsoup.connect("https://eden.dei.uc.pt/~cnl/scholar/" + linkCited).get();
					nextPage = doc.select("a[href]:has(span.gs_ico_nav_page)");
					citationsLink = doc.select("a[onclick^=window.open]");
					
					//Adding the link to all citations in a single list
					for(int j=0; j< nextPage.size(); j++){
						doc = Jsoup.connect("https://eden.dei.uc.pt/~cnl/scholar/" + linkCited).get();
						citationsLink.addAll(doc.select("a[onclick^=window.open]"));						
					}					
					
					urlCitations = new String[citationsLink.size()];
					for(int j=0; j< citationsLink.size(); j++){						
						String cit = citationsLink.get(j).attr("onclick");						
						urlCitations[j] = cit.split("'")[1];
					}					
					
					citationList = getFormattedCitations(urlCitations, citationsPerYear, authorsIgnored, titlesIgnored, rangeOfYears);
					
					if(citationList.size() != 0){
						citationInformation.getCitation().addAll(citationList);
						citationInformation.setTotalCitations(BigInteger.valueOf(citationList.size()));
											
						publication.setCitationInformation(citationInformation);
						publications.getPublication().add(publication);
					}					
					
				}//if(linkCited != "")
			}
			
			//Getting the total number of citations per year						
			Set<Integer> keys = citationsPerYear.keySet();
			
			for (Integer key : keys)
			{				
				if(key != null){
					NumberOfCitations numberOfCitations = new NumberOfCitations();					
										
					numberOfCitations.setYear(BigInteger.valueOf(key));
					numberOfCitations.setValue(BigInteger.valueOf(citationsPerYear.get(key)));
					totalCitations = totalCitations + citationsPerYear.get(key);
					citations.getNumberOfCitations().add(numberOfCitations);				
					//System.out.println(key + ": " + citationsPerYear.get(key));
				}
			}
			
			//Getting the total number of citations			
			citations.setTotal(BigInteger.valueOf(totalCitations));
			
			researcher.setCitations(citations);
			researcher.setPublications(publications);
								
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return researcher;
		
	}
	
	/**
	 * Method that returns the list of citations about a publication respecting the filters
	 * @param urlCitation stores the link to each citation  
	 * @param citationsPerYear a Map that stores the number of citations per year
	 * @param authorsIgnored stores the names of the authors whose the citations will be ignored
	 * @param titlesIgnored stores the titles of articles whose citations will be ignored
	 * @param rangeOfYears stores the range of years by filtering the citations for years
	 * @return the list of citations about a publication
	 */
	private static List<Citation> getFormattedCitations(String[] urlCitation, Map<Integer,Integer> citationsPerYear, String[] authorsIgnored, String[] titlesIgnored, int[] rangeOfYears){
		
		List<Citation> citations = new ArrayList<Citation>();
				
		try{
		
			for(int i=0; i< urlCitation.length; i++){
				
				Citation citation = new Citation();				
				boolean put = true;		
				String year;
				int y = 0;
				Document doc = Jsoup.connect("https://eden.dei.uc.pt/~cnl/scholar/" + urlCitation[i]).get();
				Elements chicagoCitation = doc.select("div#gs_cit2");
				
				citation.setFormat("Chicago");
				citation.setContent(chicagoCitation.text());						
									
				for (String author : authorsIgnored){
					if(author.length() > 0 && chicagoCitation.text().contains(author))
						put = false;			
				}
				
				for (String titleIg : titlesIgnored){
					if(titleIg.length() > 0 && chicagoCitation.text().contains(titleIg))
						put = false;
				}				
										
				year = chicagoCitation.text().substring(chicagoCitation.text().length()-5, chicagoCitation.text().length()-1);
				if(year.matches("(([1][9])|([2][0]))[0-9][0-9]")){
					y = Integer.parseInt(year);					
					if(y < rangeOfYears[0] || y > rangeOfYears[1]){
						put = false;					
					}
					else if(put){
						citation.setYear(year);
						if(!citationsPerYear.containsKey(y))
							citationsPerYear.put(y, 1);
						else
							citationsPerYear.put(y, citationsPerYear.get(y)+1);
					}				
				}
				else if(chicagoCitation.text().contains("(") && chicagoCitation.text().contains(")")){
					year = chicagoCitation.text().substring(chicagoCitation.text().lastIndexOf("(")+1, chicagoCitation.text().lastIndexOf(")"));
					if(year.matches("(([1][9])|([2][0]))[0-9][0-9]")){
						y = Integer.parseInt(year);
						if(y < rangeOfYears[0] || y > rangeOfYears[1]){
							put = false;						
						}
						else if(put){
							citation.setYear(year);
							if(!citationsPerYear.containsKey(y))
								citationsPerYear.put(y, 1);
							else
								citationsPerYear.put(y, citationsPerYear.get(y)+1);
						}					
					}
				}						
				
				if(put)
					citations.add(citation);			
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return citations;
		
	}
	
	/**
	 * This method generate a XML file with detailed information about the publications of the researcher
	 * @param researcher an object representing the XML file of the researcher
	 * @param fileName stores the name of the XML file that will be generated
	 * @param xslUrl stores the name of the XLS file
	 */
	private static void generateXml(Researcher researcher, String fileName, String xslUrl){
		
		try{			
			File file = new File(fileName );
			JAXBContext jaxbContext = JAXBContext.newInstance(Researcher.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
						
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);			
			
			jaxbMarshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
					"<?xml-stylesheet type='text/xsl' href=\"" + xslUrl + "\" ?>");			

			jaxbMarshaller.marshal(researcher, file);
			//jaxbMarshaller.marshal(researcher, System.out);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
	}

}

