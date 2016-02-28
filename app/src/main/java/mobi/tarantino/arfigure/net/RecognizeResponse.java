package mobi.tarantino.arfigure.net;

import java.util.Map;

/**
 * Created by kolipass on 08.01.16.
 */
public class RecognizeResponse {
    private Map<FaceRectangle, Integer> faceRectangle;
    private Map<Scores, Double> scores;

    enum FaceRectangle {

        left("left"),
        top("top"),
        width("width"),
        height("height"),;
        private final String text;

        /**
         * @param text
         */
        private FaceRectangle(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    enum Scores {
        anger("anger"),
        contempt("contempt"),
        disgust("disgust"),
        fear("fear"),
        happiness("happiness"),
        neutral("neutral"),
        sadness("sadness"),
        surprise("surprise"),;

        private final String text;

        /**
         * @param text
         */
        private Scores(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }
}
