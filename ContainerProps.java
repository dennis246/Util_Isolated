package harp.util;


public interface ContainerProps {
    <E> void add(E[] target, E item);

    void addAll();

    Object getFrom(int index);

    void setAt(int index, Object element);

    void insert(int from, int to);

    void remove(int from, int to);

    void replace(int from, Object element);

    void replace(Object prevElement, Object newElement);

}
