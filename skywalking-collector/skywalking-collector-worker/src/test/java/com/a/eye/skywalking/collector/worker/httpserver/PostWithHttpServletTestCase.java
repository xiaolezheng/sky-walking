package com.a.eye.skywalking.collector.worker.httpserver;

import com.a.eye.skywalking.collector.actor.LocalAsyncWorkerRef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author pengys5
 */
public class PostWithHttpServletTestCase {

    private LocalAsyncWorkerRef workerRef;
    private AbstractPost.PostWithHttpServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private PrintWriter writer;

    @Before
    public void init() throws Exception {
        workerRef = mock(LocalAsyncWorkerRef.class);
        servlet = new AbstractPost.PostWithHttpServlet(workerRef);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoPost() throws Exception {

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Integer status = (Integer)invocation.getArguments()[0];
                System.out.println(status);
                Assert.assertEquals(new Integer(200), status);
                return null;
            }
        }).when(response).setStatus(anyInt());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String reqStr = (String)invocation.getArguments()[0];
                System.out.println(reqStr);
                Assert.assertEquals("TestTest2", reqStr);
                return null;
            }
        }).when(workerRef).tell(anyString());

        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(bufferedReader.readLine()).thenReturn("Test").thenReturn("Test2").thenReturn(null);

        when(request.getReader()).thenReturn(bufferedReader);

        servlet.doPost(request, response);
    }

    @Test
    public void testDoPostError() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Integer status = (Integer)invocation.getArguments()[0];
                System.out.println(status);
                Assert.assertEquals(new Integer(500), status);
                return null;
            }
        }).when(response).setStatus(anyInt());
        doThrow(new Exception()).when(workerRef).tell(anyString());
        servlet.doPost(request, response);
    }
}
