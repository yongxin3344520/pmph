package com.bc.pmpheep.back.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * <pre>
 * 功能描述：数组工具类
 * 使用示范：
 * 
 * 
 * &#64;author (作者) nyz
 * 
 * &#64;since (该版本支持的JDK版本) ：JDK 1.6或以上
 * &#64;version (版本) 1.0
 * &#64;date (开发日期) 2017-10-19
 * &#64;modify (最后修改时间) 
 * &#64;修改人 ：nyz 
 * &#64;审核人 ：
 * </pre>
 */
public final class ArrayUtil {

	private ArrayUtil() {
	}

	/**
	 * 判断数组是否为空
	 */
	public static boolean isEmpty(Object[] array) {
		return ArrayUtils.isEmpty(array);
	}

	/**
	 * 判断数组是否非空
	 */
	public static boolean isNotEmpty(Object[] array) {
		return !isEmpty(array);
	}

	/**
	 * Array 转 List
	 */
	public static <T> List<T> toList(T[] array) {
		if (isEmpty(array)) {
			return new ArrayList<>();
		}
		return Arrays.asList(array);
	}

	// 去除数组中重复的记录
	public static String[] array_unique(String[] a) {
		// array_unique
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < a.length; i++) {
			if (!list.contains(a[i])) {
				list.add(a[i]);
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

}
