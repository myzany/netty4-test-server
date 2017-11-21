package kr.zany.sample.netty4.server.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import kr.zany.sample.netty4.server.common.data.ArgumentVo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Provider;

/**
 * <p><b>Class Description</b></p>
 * <p>Copyright ⓒ 2016 kt corp. All rights reserved.</p>
 *
 * @author Lee Sang-Hyun (zanylove@gmail.com)
 * @since 2016-01-05 18:25
 */
@Slf4j
@Named
public class SimpleServerOperator {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Inject Beans
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    private final ArgumentVo argumentVo;

    private final Provider<SimpleServerHandler> serverHandlerProvider;


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Member Variables
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructor
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public SimpleServerOperator(
            ArgumentVo argumentVo,
            Provider<SimpleServerHandler> serverHandlerProvider
    ) {
        this.argumentVo = argumentVo;
        this.serverHandlerProvider = serverHandlerProvider;
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Public Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public void run() throws Exception {

        /**
         * SO_TIMEOUT has effect only for OIO socket transport.
         *     You should use IdleStateHandler and handle an IdleStateEvent in your userEventTriggered() implementation.
         */

        EventLoopGroup boss   = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            final ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            if (argumentVo.getBacklogSize() > 0) {
                bootstrap.option(ChannelOption.SO_BACKLOG, argumentVo.getBacklogSize());
            }

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    SimpleServerHandler serverHandler = serverHandlerProvider.get();
                    ch.pipeline().addLast(serverHandler);
                }
            });

            ChannelFuture future = bootstrap.bind(argumentVo.getPort()).sync();

            System.out.println("--------------------------------------------");
            System.out.println(String.format(">>> Server started at port %d", argumentVo.getPort()));
            System.out.println(String.format("    - Disconnect after complete - %s", argumentVo.isDisconnectAfterComplete()));
            System.out.println(String.format("    - Disconnect delay millis   - %d", argumentVo.getDisconnectDelayMillis()));
            System.out.println("--------------------------------------------");

            future.channel().closeFuture().sync();  /** Waits for this future until it is done. */

        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Protected, Private Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Override Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

}
