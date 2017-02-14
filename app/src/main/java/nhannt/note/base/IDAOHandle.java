package nhannt.note.base;

import java.util.List;

/**
 * Created by nhannt on 2/8/17.
 *  An interface for communicate with database for accessing and saving data
 *
 *  It contains 5 basic methods of accessing and saving data.
 *
 *  @author nhannt
 */

public interface IDAOHandle<T, idT> {

    List<T> getAllElement();

    List<T> getListById(idT id);

    boolean insert(T obj, idT id);

    boolean update(T obj, idT id);

    boolean delete(idT noteId);

}
