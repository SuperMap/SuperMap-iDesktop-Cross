import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: lixiaoyao
 * Date: 2017/11/23
 * Time: 11:27
 * Description:
 */
public interface ITranslateFile {

	public void readFileDirectory(File file);

	public void initKeyValues();

	public void removeIllegalFiles();

	public void findUnTranslatedKey();

	public boolean isNeedAutoTranslated();

	public boolean autoTranslated();
}
