package stas.batura.radiotproject.player;

// https://gist.github.com/ianhanniballake/47617ec3488e0257325c

import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import stas.batura.radiotproject.R;

/**
 * Helper APIs for constructing MediaStyle notifications
 */
class MediaStyleHelper {
    /**
     * Build a notification using the information from the given media session. Makes heavy use
     * of {@link MediaMetadataCompat#getDescription()} to extract the appropriate information.
     *
     * @param context      Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    static NotificationCompat.Builder from(Context context, MediaSessionCompat mediaSession) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();

        if (mediaMetadata != null) {
            MediaDescriptionCompat description = mediaMetadata.getDescription();
            builder.setContentTitle(description.getTitle())
                    .setContentText(description.getSubtitle())
                    .setSubText(description.getDescription())
                    .setLargeIcon(description.getIconBitmap())
                    .setSmallIcon(R.drawable.bat_notif_icon_white)

                    .setContentIntent(controller.getSessionActivity())
                    .setDeleteIntent(
                            MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        } else {
            Log.d("mediastyle", "from: metaData: null");
        }
        return builder;
    }
}