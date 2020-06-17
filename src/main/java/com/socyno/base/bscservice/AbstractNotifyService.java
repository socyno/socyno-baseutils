package com.socyno.base.bscservice;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.socyno.base.bscmodel.ObjectMap;
import com.socyno.base.bscmodel.RunableWithSessionContext;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractNotifyService<E extends AbstractNotifyEntity> {
    
    public final static int NOEXCEPTION_TMPL_NOTFOUD = 1;
    
    protected int getTaskThreadPoolCoreSize() {
        return 1;
    }
    
    protected int getTaskThreadPoolMaximumSize() {
        return 10;
    }
    
    protected int getTaskThreadPoolQueueSize() {
        return 200;
    }
    
    @Getter
    private final ThreadPoolExecutor notifyThreadsPool;
    
    public AbstractNotifyService() {
        notifyThreadsPool = new ThreadPoolExecutor(getTaskThreadPoolCoreSize(), getTaskThreadPoolMaximumSize(), 5,
                TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(getTaskThreadPoolQueueSize()),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }
    
    protected class AsyncNotifyThread extends RunableWithSessionContext {
        private final String template;
        private final ObjectMap context;
        private final int options;
        
        AsyncNotifyThread(String template, ObjectMap context, int options) {
            this.context = context;
            this.template = template;
            this.options = options;
        }
        
        @Override
        public void exec() {
            try {
                sendNow(template, context, options);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    
    /**
     * 解析通知模板
     * 
     * @param templateCode
     *            通知模板代码
     *            
     * @param contextData
     *            通知上下文数据
     * @return
     * @throws Exception
     */
    protected abstract E parseTemplete(String templateCode, ObjectMap contextData) throws Exception ;
    
    /**
     * 发送通知内容
     * 
     * @param notifyEntity
     *                    通知内容实体
     */
    protected abstract void sendNotifyEntity(E notifyEntity) throws Exception ;
    
    /**
     * 立即发送通知
     * 
     * @param templateCode
     *                     通知模板
     * 
     * @param contextData
     *                     模板数据
     */
    public E sendNow(String templateCode, ObjectMap contextData) throws Exception {
        return sendNow(templateCode, contextData, 0);
    }
    
    /**
     * 立即发送通知
     * 
     * @param templateCode
     *                     通知模板
     * 
     * @param contextData
     *                     模板数据
     */
    public E sendNow(String templateCode, ObjectMap contextData, int options)
            throws Exception {
        return parseTemplete(templateCode, contextData);
    }
    
    /**
     * 异步发送通知
     */
    public void sendAsync(String templateCode, ObjectMap contextData) {
        sendAsync(templateCode, contextData);
    }
    
    /**
     * 异步发送通知
     */
    public void sendAsync(String templateCode, ObjectMap contextData, int options) {
        try {
            getNotifyThreadsPool().submit(this.new AsyncNotifyThread(templateCode, contextData, options));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
