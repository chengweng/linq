package com.bestvike.linq.impl;

import com.bestvike.linq.IEnumerable;
import com.bestvike.linq.IEnumerator;
import com.bestvike.linq.IEqualityComparer;
import com.bestvike.linq.IGrouping;
import com.bestvike.linq.exception.Errors;
import com.bestvike.linq.function.Func1;

/**
 * @author 许崇雷
 * @date 2017/7/11
 */
public class GroupedEnumerable<TSource, TKey, TElement> implements IEnumerable<IGrouping<TKey, TElement>> {
    private IEnumerable<TSource> source;
    private Func1<TSource, TKey> keySelector;
    private Func1<TSource, TElement> elementSelector;
    private IEqualityComparer<TKey> comparer;

    public GroupedEnumerable(IEnumerable<TSource> source, Func1<TSource, TKey> keySelector, Func1<TSource, TElement> elementSelector, IEqualityComparer<TKey> comparer) {
        if (source == null)
            throw Errors.argumentNull("source");
        if (keySelector == null)
            throw Errors.argumentNull("keySelector");
        if (elementSelector == null)
            throw Errors.argumentNull("elementSelector");
        this.source = source;
        this.keySelector = keySelector;
        this.elementSelector = elementSelector;
        this.comparer = comparer;
    }

    @Override
    public IEnumerator<IGrouping<TKey, TElement>> enumerator() {
        return Lookup.create(this.source, this.keySelector, this.elementSelector, this.comparer).enumerator();
    }
}
