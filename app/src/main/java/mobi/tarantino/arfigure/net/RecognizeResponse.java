package mobi.tarantino.arfigure.net;

import java.util.Map;

/**
 * Created by kolipass on 08.01.16.
 */
public class RecognizeResponse {
    public static final String FACE_RECTANGLE = "faceRectangle";
    public static final String SCORES = "scores";

    private Map<FaceRectangle, Integer> faceRectangle;
    private Map<Scores, Double> scores;

    public Map<FaceRectangle, Integer> getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(Map<FaceRectangle, Integer> faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public Map<Scores, Double> getScores() {
        return scores;
    }

    public void setScores(Map<Scores, Double> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "RecognizeResponse{" +
                "faceRectangle=" + faceRectangle +
                ", scores=" + scores +
                '}';
    }

    public enum FaceRectangle {

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

    public enum Scores {
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
