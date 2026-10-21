package edu.persianlearningapp.business;
import edu.persianlearningapp.dao.IDALFacade;
import edu.persianlearningapp.domain.ChapterComponent;
import java.util.List;
import edu.persianlearningapp.domain.ComponentContent;

// UC-03 BO Faseeh implements content rules  
public class ContentBO {
    private final IDALFacade dal;
    public ContentBO(IDALFacade dal) { this.dal = dal; }
    public List<ChapterComponent> listConfiguredComponents(int chapterId) { 
    	if(chapterId<=0) {
    		throw new BusinessException("Invalid chp ID.");
    	}
    	if(dal.findById(chapterId)==null) {
    		throw new BusinessException("Chp not found");
    	}
    	return dal.findComponentsByChapterId(chapterId);
    }
    public void saveContent(int chapterId, int componentId, String persian, String urdu) { 
    	if(componentId<=0) {
    		throw new BusinessException("invalid componentID");
    		
    	}
    	List<ChapterComponent> components=listConfiguredComponents(chapterId);
    	
    	boolean found=false;
    	for(ChapterComponent component:components) {
    		if(component.getId()==componentId) {
    			found=true;
    			break;
    		}
    	}
    	if(!found) {
    		throw new BusinessException("Component not found in chp");
    	}
    	if ((persian == null || persian.trim().isEmpty()) &&
    		    (urdu == null || urdu.trim().isEmpty())) {

    		    throw new BusinessException("Enter Urdu or Persian content.");
    		}
    	if ((persian == null || persian.trim().isEmpty()) &&
    		    (urdu == null || urdu.trim().isEmpty())) {

    		    throw new BusinessException("فارسی یا اردو میں مواد درج کریں۔");
    		}

    		if (!isPersianUrduText(persian)) {
    		    throw new BusinessException(
    		        "فارسی متن میں انگریزی یا اعداد کی اجازت نہیں ہے۔");
    		}

    		if (!isPersianUrduText(urdu)) {
    		    throw new BusinessException(
    		        "اردو متن میں انگریزی یا اعداد کی اجازت نہیں ہے۔");
    		}
    	boolean saved= dal.saveContent(chapterId, componentId, persian, urdu);
    	
    	if(!saved) {
    		throw new BusinessException("content could NOT be Saved.");
    	}
    		
    }

	private boolean isPersianUrduText(String text) {
	
	    // One language field may be empty
	    if (text == null || text.trim().isEmpty()) {
	        return true;
	    }
	
	    boolean hasLetter = false;
	
	    for (int i = 0; i < text.length();) {
	
	        int c = text.codePointAt(i);
	        i += Character.charCount(c);
	
	        // Reject numbers
	        if (Character.isDigit(c)) {
	            return false;
	        }
	
	        // Accept Arabic-script letters used in Persian and Urdu
	        if (Character.UnicodeScript.of(c)
	                == Character.UnicodeScript.ARABIC
	                && Character.isLetter(c)) {
	
	            hasLetter = true;
	            continue;
	        }
	
	        // Accept Arabic vowel marks
	        int type = Character.getType(c);
	
	        if ((type == Character.NON_SPACING_MARK
	                || type == Character.COMBINING_SPACING_MARK)
	                && Character.UnicodeBlock.of(c)
	                == Character.UnicodeBlock.ARABIC) {
	            continue;
	        }
	
	        // Allow spaces, punctuation and joined words
	        if (Character.isWhitespace(c)
	                || Character.isSpaceChar(c)
	                || c == 0x200C
	                || c == 0x200D
	                || ".,!?;:()[]-«»“”'\"،؛؟۔".indexOf(c) >= 0) {
	            continue;
	        }
	
	        // Reject English and other unsupported characters
	        return false;
	    }
	
	    return hasLetter;
	}

    public void removeContent(int chapterId, int componentId) { 
    	
    	if (componentId <= 0) {
            throw new BusinessException("Invalid component ID.");
        }

        List<ChapterComponent> components = listConfiguredComponents(chapterId);

        boolean found = false;
        for (ChapterComponent component : components) {
            if (component.getId() == componentId) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new BusinessException("Component not found in this chapter.");
        }
        boolean deleted = dal.deleteContent(chapterId, componentId);
        if (!deleted) {
            throw new BusinessException("No saved content found to remove.");
        }	
    }
// jab admin kye component gui say select kray ga tou getContent sara data fetch krva kay day ga
	public ComponentContent getContent(int chapterId, int componentId) {
	
	    if (componentId <= 0) {
	        throw new BusinessException("Invalid component ID.");
	    }
	
	    List<ChapterComponent> components = listConfiguredComponents(chapterId);
	
	    for (ChapterComponent component : components) {
	        if (component.getId() == componentId) {
	            return dal.findContent(chapterId, componentId);
	        }
	    }
	
	    throw new BusinessException("Component not found in this chapter.");
	}

}
