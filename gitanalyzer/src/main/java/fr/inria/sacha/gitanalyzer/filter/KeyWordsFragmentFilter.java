package fr.inria.sacha.gitanalyzer.filter;


/**
 * A filter to allow only fragments <b>containing</b> expected word(s)
 * Apply on lone fragment level
 *
 */
public class KeyWordsFragmentFilter extends AbstractFilter {
	
	private String [] keywords;
	
	public KeyWordsFragmentFilter(String... keywords){
		super();
		this.keywords = keywords;
	}
	
	public KeyWordsFragmentFilter(IFilter parentFilter, String... keywords){
		super(parentFilter);
		this.keywords = keywords;
	}
	
	
	
	/**
	 * Check if the fragment contain at least one of the word
	 * @return
	 */
	@Override
	public boolean acceptFragment(String fragment){
		
		if (super.acceptFragment(fragment)){
			for (String keyword : this.keywords) {
				if (fragment.contains(keyword)){
					return true;
				}
			}
			return false;
		}
		else return false;
	}
	
	
}
