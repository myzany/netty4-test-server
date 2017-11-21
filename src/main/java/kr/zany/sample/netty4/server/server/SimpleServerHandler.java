package kr.zany.sample.netty4.server.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import kr.zany.sample.netty4.server.common.data.ArgumentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p><b>Class Description</b></p>
 * <p>Copyright ⓒ 2016 kt corp. All rights reserved.</p>
 *
 * @author Lee Sang-Hyun (zanylove@gmail.com)
 * @since 2016-01-05 18:21
 */
@Slf4j
@Named
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SimpleServerHandler extends ChannelInboundHandlerAdapter {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inject Beans
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final ArgumentVo argumentVo;

    private final String activeProfile;


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Member Variables
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private static AtomicLong currentActiveCount = new AtomicLong(0);

    private static AtomicLong accumActiveCount = new AtomicLong(0);

    private static AtomicLong accumInactiveCount = new AtomicLong(0);


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructor
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Inject
    public SimpleServerHandler(
            ArgumentVo argumentVo,
            @Value("${spring.profiles.active}") String activeProfile
    ) {
        this.argumentVo = argumentVo;
        this.activeProfile = activeProfile;
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected, Private Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private void tryDisconnect(ChannelHandlerContext ctx) throws InterruptedException {

        /** 접속 해제 시도 : 설정에 따라 접속을 끊을수도 있고 안끊을수도 있음 */

        if (argumentVo.isDisconnectAfterComplete()) {

            if (argumentVo.getDisconnectDelayMillis() > 0) {
                Thread.sleep(argumentVo.getDisconnectDelayMillis());
            }

            ctx.disconnect();
            ctx.channel().close();
            ctx.close();
        }
    }

    private void sendMessage(ChannelHandlerContext ctx, final byte[] msgByte) throws InterruptedException {

        /** 메시지 전송 */

        if (argumentVo.getSendDelayMillis() > 0) {
            Thread.sleep(argumentVo.getSendDelayMillis());
        }

        final ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msgByte);

        final ChannelFuture channelFuture = ctx.writeAndFlush(buf);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert channelFuture == future;
                log.debug(String.format(">>> Message Sent '%s' : %s", activeProfile, new String(msgByte)));
            }
        });
    }

    private String currentChannelStatus(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return String.format("id:%s registered:%s active:%s open:%s writable:%s",
                channel.id(),
                channel.isRegistered(),
                channel.isActive(),
                channel.isOpen(),
                channel.isWritable()
        );
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Override Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * CONNECT     : channelRegistered -> channelActive ->
     * COMMUNICATE : [channelRead -> channelReadComplete] ->
     * DISCONNECT  : channelInactive -> channelUnregistered
     */

    /** channel register */

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        log.debug(String.format(">>> [Inbound] channelRegistered - %s", currentChannelStatus(ctx)));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        log.debug(String.format(">>> [Inbound] channelUnregistered - %s", currentChannelStatus(ctx)));
    }


    /** channel active */

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        String logMessage = String.format(">>> [Inbound] channelActive '%s' (cac:%4d ac:%4d ic:%4d) - %s",
                ctx.channel().remoteAddress(),
                currentActiveCount.incrementAndGet(),
                accumActiveCount.incrementAndGet(),
                accumInactiveCount.longValue(),
                currentChannelStatus(ctx)
        );

        log.info(logMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        String logMessage = String.format(">>> [Inbound] channelInactive '%s' (cac:%4d ac:%4d ic:%4d) - %s",
                ctx.channel().remoteAddress(),
                currentActiveCount.decrementAndGet(),
                accumActiveCount.longValue(),
                accumInactiveCount.incrementAndGet(),
                currentChannelStatus(ctx)
        );

        log.info(logMessage);
    }


    /** channel read */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug(String.format(">>> [Inbound] channelRead - %s", currentChannelStatus(ctx)));
        ByteBuf in = (ByteBuf) msg;
        try {
            StringBuilder builder = new StringBuilder();
            while (in.isReadable()) {
                builder.append((char)in.readByte());
            }
            log.debug(String.format("Incoming Message : %s", builder.toString()));
        } finally {
            in.release();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        log.debug(String.format(">>> [Inbound] channelReadComplete - %s", currentChannelStatus(ctx)));

        sendMessage(ctx, argumentVo.getSendMessage().getBytes());
        tryDisconnect(ctx);
    }


    /** exception */

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(String.format("cause:%s message:%s", cause.getCause(), cause.getMessage()));
        tryDisconnect(ctx);
    }
}
