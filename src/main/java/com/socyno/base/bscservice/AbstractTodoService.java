package com.socyno.base.bscservice;

public abstract class AbstractTodoService {
    
    /**
     * 创建待办事项清单。要求 todoTypeKey 和 todoTypeId 的组合必须是未关闭待办事项的唯一标识。
     * 
     * @param todoTypeKey
     *          待办项类型标识
     * @param todoTypeId
     *          待办项流程单编号
     * @param applierId
     *          待办项流程单发起人
     * @param targetPage
     *          待办项流程单详情页面
     * @param title
     *          待办项标题
     * @param category
     *          待办项分类。预留字段，用于扩展用途。
     * @param assignee
     *          待办项可受理人员清单
     */
    public abstract long createTodo(String todoTypeKey, Object todoTypeId, Long applierId, String targetPage, String title, String category,
            long... assignee) throws Exception;
    
    /**
     * 关闭待办事项
     * 
     * @param todoTypeKey
     *          待办项类型标识
     * @param todoTypeId
     *          待办项流程单编号
     * @param result
     *          处理结果。如审批拒绝，操作完成，操作失败等等。
     */
    public abstract void closeTodo(String todoTypeKey, Object targetTypeId, String result) throws Exception;
}
