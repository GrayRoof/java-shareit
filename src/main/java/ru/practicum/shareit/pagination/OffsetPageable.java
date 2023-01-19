package ru.practicum.shareit.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.Exception.NotValidException;

public class OffsetPageable implements Pageable {

    private final int offset;
    private final int limit;
    private final Sort sort;

    protected OffsetPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(int offset, int limit, Sort sort) {
        if (offset < 0 || limit < 1) {
            throw new NotValidException("Параметры постраничного вывода введены некорректно: \n" +
                    "начальная позиция не может быть меньше 0 \n" +
                    "количество записей на странице не может быть меньше 1!");
        }
        return new OffsetPageable(offset, limit, sort);
    }

    @Override
    public int getPageNumber() {

        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {

        return offset;
    }

    @Override
    public Sort getSort() {

        return sort;
    }

    @Override
    public Pageable next() {

        return of(getPageSize(), (int) (getOffset() + getPageSize()), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
                ? of(getPageSize(), (int) (getOffset() - getPageSize()), getSort())
                : first();
    }

    @Override
    public Pageable first() {

        return of(getPageSize(), 0, getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {

        return of(getPageSize() * pageNumber, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {

        return getOffset() >= getPageSize();
    }
}
