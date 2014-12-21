package orxanimeditor.data.v1;

public interface HierarchicalData {
	public void remove();
	public String getName();
	public void setName(String name);
	public Object getParent();
	public Object[] getPath();
	public int move(Object newParent, int currentIndexOfPreviousItem);
}
