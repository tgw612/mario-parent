//package com.mario.common.util;
//
//import com.baomidou.mybatisplus.plugins.Page;
//import com.mario.common.model.request.PageQueryRequest;
//import com.mall.common.request.PageRequest;
//import com.mall.common.response.PageResult;
//
//import java.util.List;
//import java.util.function.Function;
//
//public class PageUtils {
//
//    public static Page toPage(PageRequest pageRequest) {
//        return pageRequest.getPage();
//    }
//
//    public static <T> Page<T> toPage(PageRequest pageRequest, Function<Page<T>, List<T>> function) {
//        Page<T> page = toPage(pageRequest);
//        List<T> records = function.apply(page);
//        page.setRecords(records);
//        return page;
//    }
//
//    public static <T> PageResult<T> toPageResult(PageRequest pagerRequest, Function<Page<T>, List<T>> function) {
//        Page<T> page = toPage(pagerRequest, function);
//        PageResult<T> pageResult = new PageResult<>();
//        pageResult.setPageOffset(page.getCurrent());
//        pageResult.setPageSize(page.getSize());
//        pageResult.setDatas(page.getRecords());
//        // pageResult.setTotalPage((int) page.getPages());
//        pageResult.setTotalRecord((int) page.getTotal());
//        return pageResult;
//    }
//
//    /**
//     * 获取分页起始值
//     * @param request
//     * @return
//     */
//    public static int getStartPage(PageQueryRequest request) {
//        return getStartPage(request.getCurrentPage(), request.getPageSize());
//    }
//
//    /**
//     * 获取分页起始值
//     * @return
//     */
//    public static int getStartPage(int currentPage, int pageSize) {
//        return (currentPage - 1) * pageSize;
//    }
//
//    /**
//     * 获取总页数
//     * @param pageSize
//     * @param count
//     * @return
//     */
//    public static int getPageCount(Integer pageSize, long count) {
//        return (int) (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
//    }
//
//}
