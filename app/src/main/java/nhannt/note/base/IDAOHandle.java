package nhannt.note.base;

import java.util.List;

/**
 * Created by nhannt on 2/8/17.
 */

public interface IDAOHandle<T, idT> {

    public List<T> getAllElement();

    public List<T> getListById(idT id);

    public boolean insert(T obj, idT id);

    public boolean update(T obj, idT id);

    public boolean delete(idT noteId);

}
