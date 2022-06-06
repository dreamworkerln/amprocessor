package ru.kvanttelecom.tv.amprocessor.flexporter.data.schema.prometheus;

public class prometheus_series {

    public static class flussonic {

        /**
         * Prometheus expressions
         */
        public static class expressions {
            //public static final String flussonic_stream_bytes_in = "flussonic_stream_bytes_in";
            public static final String rate_flussonic_stream_bytes_in_5m = "rate(flussonic_stream_bytes_in[5m])";
            public static final String rate_flussonic_stream_bytes_in_15s = "rate(flussonic_stream_bytes_in[15s])";
            public static final String down_streams_15s = "rate(flussonic_stream_bytes_in[15s]) == 0";
            public static final String up_streams_15s = "rate(flussonic_stream_bytes_in[15s]) != 0";
            public static final String stream_alive_1h_999 = "avg_over_time(stream_alive[1h]) >= 0.999";
        }

        /**
         * Tags names
         */
        public static class tags {
            public static final String stream =    "stream";
            public static final String title =     "title";
            public static final String instance =  "instance";
            public static final String server_id = "server_id";
        }

        /**
         * Metrics
         */
        public static class metrics {

            public static class stream {

                public static class alive {
                    public static final String name = "stream_alive";
                    public static final String description = "Stream healthy";
                }


                public static class flapping {

//                    public static class frequency {
//                        public static final String name =  "stream_flapping_frequency";
//                        public static final String description =    "Stream flapping frequency";
//                    }

                    public static class down {
                        public static final String name =  "stream_flapping_down";
                        public static final String description =    "Stream flapping down count";
                    }




                }



            }
        }
    }
}
