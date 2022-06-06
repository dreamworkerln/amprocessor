package ru.kvanttelecom.tv.amprocessor.core.amqp;

/**
 * AMQP queues, exchangers, binging Beans identifiers
 */
public class AmqpIds {

    public static class queue {

        // Alert
        public static class alert {

            // messages
            public static class msg {
                // new alert
                public static final String new_alert = "queue.alert.msg.new";
            }

            // RPC get all currently firing
            public static class rpc {
                public static final String exec =   "queue.alert.rpc.exec";
            }
        }
    }

    public static class exchanger {

        public static class alert {

            public static class msg {
                // new alert
                public static final String new_alert = "exchanger.alert.msg.new";
            }

            // RPC get all currently firing
            public static class rpc {
                public static final String exec = "exchanger.alert.rpc.exec";
            }
        }
    }


    // Streams
    public static class binding {

        public static class alert {

            public static class msg {
                // new alert
                public static final String new_alert = "binding.alert.msg.new";
            }



            // RPC get all currently firing
            public static class rpc {
                public static final String exec = "binding.alert.rpc.exec";
            }
        }
    }

}
