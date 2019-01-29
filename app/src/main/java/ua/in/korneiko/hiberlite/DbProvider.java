package ua.in.korneiko.hiberlite;

import java.util.List;

public interface DbProvider<T> {

    public List<T> findAll();

    public T find(long id);

    public List<T> find(T item);

    public List<T> filter(T item);

    public long add(T item);

    public boolean edit(int id, T item);

    public List<Long> addAll(Iterable<? extends T> items);

    public boolean remove(long id);

    public boolean remove(T value);

    public int clear();

    public int getCount();
}
