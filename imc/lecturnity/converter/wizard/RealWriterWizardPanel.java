package imc.lecturnity.converter.wizard;

import imc.epresenter.converter.ConversionDisplay;

import imc.epresenter.filesdk.FileResources;
import imc.epresenter.filesdk.util.Localizer;

import imc.lecturnity.converter.DocumentData;
import imc.lecturnity.converter.ProfiledSettings;
import imc.lecturnity.converter.RealMediaConverter;
import imc.lecturnity.converter.LPLibs;
import imc.lecturnity.converter.ProgressManager;
import imc.lecturnity.converter.StreamingMediaConverter;

import imc.lecturnity.util.wizards.Wizard;
import imc.lecturnity.util.wizards.WizardData;
import imc.lecturnity.util.wizards.WizardPanel;
import imc.lecturnity.util.NativeUtils;

import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RealWriterWizardPanel extends WizardPanel
   implements ConversionDisplay
{
   private static String HEADER = "[!]";
   private static String SUBHEADER = "[!]";
   private static String CONVERT_ERROR = "[!]";
   private static String CANCELLED = "[!]";
   private static String ERROR = "[!]";
   private static String INFO = "[!]";
   private static String CONV_SUCCESS = "[!]";
   private static String CANCEL = "[!]";
   private static char   MNEM_CANCEL = '?';

   private static String PIX_PROGRESS = "[!]";
   private static String AUDIO_PROGRESS = "[!]";
   private static String VIDEO_PROGRESS = "[!]";
   private static String CONVERT_AV = "[!]";
   private static String CONVERT = "[!]";
   private static String READING_DATA = "[!]";
   private static String PROGRESS = "[!]";
   private static String REMAINING = "[!]";
   private static String OF = "[!]";
   private static String START = "[!]";
   private static String COPYING_TO_SERVER_MESSAGE = "[!]";
   private static String CREATING_HTML = "[!]";
   private static String CREATING_SCORM = "[!]";
   private static String CREATING_IMAGES = "[!]";
   private static String TRANSFERRING_FILES = "[!]";
   private static String TRANSFERRING_STREAMING_SERVER = "[!]";
   private static String WAIT_FOR_CANCEL = "[!]";
   private static String CHECK_UPLOAD_SERVER = "[!]";
   private static String NOT_RUNNABLE = "[!]";

   private static Localizer localizer_;

   static
   {
      try
      {
         Localizer l = new Localizer
            ("/imc/lecturnity/converter/StreamingMediaConverter_",
             "en");

         COPYING_TO_SERVER_MESSAGE = l.getLocalized("COPYING_TO_SERVER_MESSAGE");
         CREATING_HTML = l.getLocalized("CREATING_HTML");
         CREATING_SCORM = l.getLocalized("CREATING_SCORM");
         CREATING_IMAGES = l.getLocalized("CREATING_IMAGES");
         TRANSFERRING_FILES = l.getLocalized("TRANSFERRING_FILES");
         TRANSFERRING_STREAMING_SERVER = l.getLocalized("TRANSFERRING_STREAMING_SERVER");
         WAIT_FOR_CANCEL = l.getLocalized("WAIT_FOR_CANCEL");

         l = new Localizer
            ("/imc/lecturnity/converter/wizard/RealWriterWizardPanel_",
             "en");
         
         HEADER = l.getLocalized("HEADER");
         SUBHEADER = l.getLocalized("SUBHEADER");
         ERROR = l.getLocalized("ERROR");
         INFO = l.getLocalized("INFO");
         CONVERT_ERROR = l.getLocalized("CONVERT_ERROR");
         CANCELLED = l.getLocalized("CANCELLED");
         CONV_SUCCESS = l.getLocalized("CONV_SUCCESS");
         CANCEL = l.getLocalized("CANCEL");
         MNEM_CANCEL = l.getLocalized("MNEM_CANCEL").charAt(0);

         PIX_PROGRESS = l.getLocalized("PIX_PROGRESS");
         AUDIO_PROGRESS = l.getLocalized("AUDIO_PROGRESS");
         VIDEO_PROGRESS = l.getLocalized("VIDEO_PROGRESS");
         CONVERT_AV = l.getLocalized("CONVERT_AV");
         CONVERT = l.getLocalized("CONVERT");
         READING_DATA = l.getLocalized("READING_DATA");
         PROGRESS = l.getLocalized("PROGRESS");
         REMAINING = l.getLocalized("REMAINING");
         OF = l.getLocalized("OF");
         START = l.getLocalized("START");
         COPYING_TO_SERVER_MESSAGE = l.getLocalized("CHECK_UPLOAD_SERVER");
         CHECK_UPLOAD_SERVER = l.getLocalized("CHECK_UPLOAD_SERVER");
         NOT_RUNNABLE = l.getLocalized("NOT_RUNNABLE");

         localizer_ = l;
      }
      catch (IOException e)
      {
         JOptionPane.showMessageDialog(null, "Could not open Locale database!\n" +
                                       "RealWriterWizardPanel\n" +
                                       e.toString(),
                                       "Error", JOptionPane.ERROR_MESSAGE);
         System.exit(1);
      }
   }

   private PublisherWizardData m_pwData = null;
   private RealMediaConverter realMediaConverter_ = null;
   private WizardPanel  nextWizard_ = null;

   private JLabel            m_progressLabel    = null;
   private JProgressBar      m_progressBar      = null;
   private ProgressManager   m_progressManager  = null;
   private ProgressBarThread m_pbt              = null;

   private int clipStep_   = 0;
   private int fileSize_   = 0;

   private String strHtml_ = null;
   private String VIDCLIPS = " Video  ";

   private JButton cancelButton_ = null;

   public RealWriterWizardPanel(PublisherWizardData pwData)
   {
      super();
      
      m_pwData = pwData;
      m_progressManager = m_pwData.m_progressManager;

      setPreferredSize(new Dimension(500, 400));

      addButtons(BACK_QUIT_CANCEL);

      if (NativeUtils.isLibraryLoaded())
      {
         addButtons(BUTTON_CUSTOM, START, 's');
         setEnableButton(BUTTON_CUSTOM, false);
      }

      useHeaderPanel(HEADER, SUBHEADER,
                     "/imc/lecturnity/converter/images/real_networks_logo.gif");

      Container r = getContentPanel();
      r.setLayout(null);

      m_progressLabel = new JLabel("");
      if (imc.lecturnity.converter.ConverterWizard.USE_CUSTOM_COLORS)
         m_progressLabel.setBackground(Wizard.BG_COLOR);
      m_progressLabel.setSize(460, 20);
      m_progressLabel.setLocation(20, 20);
      r.add(m_progressLabel);

      m_progressBar = new JProgressBar(JProgressBar.HORIZONTAL,
                                       0, 100);
      if (imc.lecturnity.converter.ConverterWizard.USE_CUSTOM_COLORS)
         m_progressBar.setBackground(Wizard.BG_COLOR);
      m_progressBar.setLocation(20, 45);
      m_progressBar.setSize(460, 20);
      r.add(m_progressBar);

      cancelButton_ = new JButton(CANCEL);
      if (imc.lecturnity.converter.ConverterWizard.USE_CUSTOM_COLORS)
         cancelButton_.setBackground(Wizard.BG_COLOR);
      cancelButton_.setSize(100, 25);
      cancelButton_.setLocation(380, 70);
      cancelButton_.setMnemonic(MNEM_CANCEL);
      cancelButton_.addActionListener(new CancelButtonListener());
      r.add(cancelButton_);
   }

   public String getDescription()
   {
      // BUGFIX 4982: set title in direct mode
      return "Publisher";
      // return "";
   }

   public PublisherWizardData getWizardData()
   {
      m_pwData.nextWizardPanel = null;
      m_pwData.finishWizardPanel = null;

      if (m_pwData.B_GOTO_START_DIALOG)
      {
         m_pwData.finishWizardPanel = null;
         // Bugfix 5448:
         // If Start Dialog is not defined then try the TargetUpload Dialog 
         // If TargetUpload Dialog is not defined then create a new one
         if (m_pwData.GetStartDialog() != null) {
            m_pwData.nextWizardPanel = m_pwData.GetStartDialog();
         } else {
            // Next line outcommented. Reason: 'getWizardData()' is called twice (why?)
            // and then 'nextWizardPanel' will be set to 'null' after the 2nd call 
            //m_pwData.B_GOTO_START_DIALOG = false;
            if (m_pwData.GetUploadDialog() != null) {
               m_pwData.nextWizardPanel = m_pwData.GetUploadDialog();
            } else {
               TargetUploadWizardPanel tuwp = new TargetUploadWizardPanel(m_pwData);
               m_pwData.SetUploadDialog(tuwp);
               m_pwData.nextWizardPanel = tuwp;
            }
         }
      }

      return m_pwData;
   }

   public void refreshDataDisplay()
   {
       super.refreshDataDisplay();
   }

   public int displayWizard()
   {
      ProfiledSettings ps = m_pwData.GetProfiledSettings();
      DocumentData dd = m_pwData.GetDocumentData();

      String strLrdFilename = dd.GetPresentationFileName();
      String strFcc = m_pwData.m_strFccCodec;
      boolean bScormEnabled = ps.GetBoolValue("bScormEnabled");

      setEnableButton(BACK_QUIT_CANCEL, false);

      if (NativeUtils.isLibraryLoaded())
         setEnableButton(BUTTON_CUSTOM, false);

      cancelButton_.setEnabled(true);

      refreshDataDisplay();

      m_progressBar.setValue(0);
      
      strHtml_ = null;

      String strError = "";
     
      int result = 0;
      try
      {
         FileResources resources = 
            FileResources.createFileResources(strLrdFilename);

         realMediaConverter_ = new RealMediaConverter(m_pwData, resources, this);

         boolean bDoUpload = ( (ps.GetIntValue("iReplayType") == PublisherWizardData.REPLAY_SERVER) 
                              && (ps.GetIntValue("iTransferType") == PublisherWizardData.TRANSFER_UPLOAD) );
         boolean bUseNetworkDrive = ( (ps.GetIntValue("iReplayType") == PublisherWizardData.REPLAY_SERVER) 
                                     &&(ps.GetIntValue("iTransferType") == PublisherWizardData.TRANSFER_NETWORK_DRIVE) );
         boolean bUseStreamingServer = 
            ps.GetIntValue("iReplayType") == PublisherWizardData.REPLAY_SERVER && 
            ps.GetIntValue("iServerType") == PublisherWizardData.SRV_STREAMING_SERVER;
         boolean bSeparateClips = ps.GetBoolValue("bShowClipsOnSlides");
         boolean bHasClips = dd.HasClips() && bSeparateClips;
         int iClipCount = dd.HasClips() ? dd.GetVideoClipInfos().length : 0;

         // For the ProgressManager:
         m_progressBar.setMaximum(100);
         defineProgressBarSteps(bDoUpload, bUseNetworkDrive, bUseStreamingServer, 
                                bScormEnabled, bHasClips, iClipCount);
         m_progressManager.SetCurrentSubProgress(0.0f);
         
         // BUGFIX 5140: Direct Publish should use different dir (analog to Profile Mode)
         if (m_pwData.m_bIsProfileMode || m_pwData.m_bIsDirectMode || bDoUpload || bUseNetworkDrive)
         {
            if (bDoUpload)
            {
               m_progressLabel.setText(CHECK_UPLOAD_SERVER);
               m_progressManager.SetProgressStep("checkUpload");
               m_progressManager.SetProgressLabel(m_progressLabel.getText());

               if (!m_pwData.m_bIsUploadSessionRunning)
               {
                  // Start the Uploader Session
                  UploaderAccess.StartSession();
				  if (ps.GetIntValue("iServerType") == PublisherWizardData.SRV_SLIDESTAR)
					  UploaderAccess.IsSlidestar(true);
				  else
					  UploaderAccess.IsSlidestar(false);
                  m_pwData.m_bIsUploadSessionRunning = true;

                  // check access and collect passwords (if necessary); also shows error message
                  result = UploaderAccess.CheckUpload(true, ps, this);

                  if (result == 0 && bUseStreamingServer)
                     result = UploaderAccess.CheckUpload(false, ps, this);
               }
            }

            // Use generic paths and names for Profile/Upload/Network drive
            if (result >= 0)
               result = SelectPresentationWizardPanel.DetermineTargetPathsAndFiles(m_pwData, this);
         }

         // BUGFIX 5236: Check length of target path and files
         int hrPathCheck = 0;
         if (result >= 0)
            hrPathCheck = SelectPresentationWizardPanel.CheckTargetPathFileLength(m_pwData, this);
         if (hrPathCheck == LPLibs.CANCELLED)
            result = RealMediaConverter.CANCELLED;
      
         if (result != 0 && result != RealMediaConverter.CANCELLED)
         {
            if (result > 0)
            {
               // "Cancel" was pressed --> "Re"set result
               result = RealMediaConverter.CANCELLED;
            }
            else
            {
               System.out.println("!!! Upload error in RealWriterWizardPanel::displayWizard()!");

               // Finish Uploader Session
               UploaderAccess.FinishSession();
               m_pwData.m_bIsUploadSessionRunning = false;

               int res = UploaderAccess.ShowErrorMessageForErrorCode(result, (WizardPanel)this);

               if (res == 0) // "Verify Settings"
               {
                  m_pwData.B_GOTO_START_DIALOG = true;

                  realMediaConverter_ = null;
                  doNext();
                  return BUTTON_NEXT;
               }
               else if (res == 1) // "Retry"
               {
                  return displayWizard();
               }
               else // "Cancel" or Generic error
               {
                  // do nothing special, process is aborted
               }
            }
         }

         if (result >= 0) {
            result = realMediaConverter_.checkStructuredScreengrabbing();
         }
         
         if (result >= 0)
         {
            m_pbt = new ProgressBarThread(realMediaConverter_);
            m_pbt.start();

            result = realMediaConverter_.startConvert();

            m_pbt.requestStop();
         }

         switch (result)
         {
         case RealMediaConverter.SUCCESS:
            strHtml_ = realMediaConverter_.getPathFileName() + ".html";

            setEnableButton(BUTTON_BACK | BUTTON_CANCEL, false);
            setEnableButton(BUTTON_QUIT, true);

            if (NativeUtils.isLibraryLoaded() && !bScormEnabled)
               setEnableButton(BUTTON_CUSTOM, true);

/*
            JOptionPane.showMessageDialog(this, CONV_SUCCESS, INFO,
                                       JOptionPane.INFORMATION_MESSAGE);
*/
            if (!m_pwData.m_bIsLiveContext) {
                String strTargetDir = ps.GetStringValue("strTargetDirLocal");
                String[] opt = {localizer_.getLocalized("OK"), localizer_.getLocalized("OPEN_DIR")};
                String strConversionSucceeded = localizer_.getLocalized("CONV_SUCCESS");
                if (bDoUpload || bUseNetworkDrive)
                   strConversionSucceeded = localizer_.getLocalized("CONV_UPLOAD_SUCCESS");
                int jres = JOptionPane.showOptionDialog(this,
                                                        (strConversionSucceeded 
                                                        + strTargetDir + "\n\n"), 
                                                        localizer_.getLocalized("INFO"), 
                                                        JOptionPane.DEFAULT_OPTION, 
                                                        JOptionPane.INFORMATION_MESSAGE, 
                                                        null, opt, opt[0]);
                if (jres == JOptionPane.NO_OPTION) {
                   NativeUtils.startFile(strTargetDir);
                }
            }

            break;

         default:
            boolean bHasUploadError = false;
            // If upload was successful then still an upload session is running - this is finished later
            if (bDoUpload && !m_pwData.m_bIsUploadSessionRunning)
               bHasUploadError = true;
            if (!bHasUploadError) // An upload error message is already thrown
            {
                // Bugfix 5403: Check for "disk full" error
                boolean bIsDiskFull = false;
                String strTargetPath = ps.GetStringValue("strTargetDirLocal");
                int nMinKb = 100*1024; // less than 100 kB left
                if (NativeUtils.getSpaceLeftOnDevice(strTargetPath) < nMinKb)
                    bIsDiskFull = true;

               try
               {
                  strError = localizer_.getLocalized("" + result);
                  if (bIsDiskFull) {
                      // Two error codes (-4, -15) already include "disk full" as possible cause
                      // --> Replace error message by "disk full" message
                      if (result == StreamingMediaConverter.ENCODING_FAILED) // -4
                          strError = localizer_.getLocalized("DISK_FULL");
                      else if (result == StreamingMediaConverter.RPM_FAILED) // -15
                          strError = localizer_.getLocalized("DISK_FULL");
                      else // Append "disk full" message
                          strError = strError + "\n" + localizer_.getLocalized("DISK_FULL");
                  }
               }
               catch (Exception e)
               {
                  strError = localizer_.getLocalized("UNKNOWN") + result;
                  if (bIsDiskFull)
                      strError = localizer_.getLocalized("DISK_FULL");
               }
               if (strError == null) {
                  strError = localizer_.getLocalized("UNKNOWN") + result;
                  if (bIsDiskFull)
                      strError = localizer_.getLocalized("DISK_FULL");
               }

               if (result != RealMediaConverter.CANCELLED)
               {
                  if (result == RealMediaConverter.DECODING_FAILED)
                     JOptionPane.showMessageDialog(this, 
                                                   strError + strFcc, 
                                                   ERROR,
                                                   JOptionPane.ERROR_MESSAGE);
                  else
                     JOptionPane.showMessageDialog(this, strError, ERROR,
                                                   JOptionPane.ERROR_MESSAGE);
               }
               else
               {
                  JOptionPane.showMessageDialog(this, strError, INFO,
                                                JOptionPane.INFORMATION_MESSAGE);
               }
            }

            setEnableButton(BUTTON_BACK | BUTTON_CANCEL, true);
            setEnableButton(BUTTON_QUIT, false);

            if (NativeUtils.isLibraryLoaded())
               setEnableButton(BUTTON_CUSTOM, false);
         }

         if (bDoUpload)
         {
            // Finish Uploader Session
            UploaderAccess.FinishSession();
            m_pwData.m_bIsUploadSessionRunning = false;
         }
      }
      catch (IOException e)
      {
         JOptionPane.showMessageDialog(this, CONVERT_ERROR + '\n' +
                                       e.toString(), ERROR,
                                       JOptionPane.ERROR_MESSAGE);

         setEnableButton(BUTTON_BACK | BUTTON_CANCEL, true);
         setEnableButton(BUTTON_QUIT, false);

         if (NativeUtils.isLibraryLoaded())
            setEnableButton(BUTTON_CUSTOM, false);
      }
      finally
      {
         realMediaConverter_ = null;
         cancelButton_.setEnabled(false);
      }

      // LiveContext mode: close the Publisher automatically 
      if (m_pwData.m_bIsLiveContext) {
         super.doQuitOnDoQuit(); // Signal that super should return from displayWizard()

         if (result != RealMediaConverter.CANCELLED)
             doQuit(); // Set the correct "action" value: QUIT (not the default CANCEL)
      }
      // Note: This is double code to the other formats

      return super.displayWizard();
   }

   private void requestCancel()
   {
      cancelButton_.setEnabled(false);
      if (realMediaConverter_ != null)
         realMediaConverter_.cancelConvert();
   }

   protected void customCommand(String command)
   {
      // Check if the server type enables replay
      ProfiledSettings ps = m_pwData.GetProfiledSettings();
      boolean bIsRunnableServerType 
         = ( (ps.GetIntValue("iServerType") == PublisherWizardData.SRV_FILE_SERVER)
            || (ps.GetIntValue("iServerType") == PublisherWizardData.SRV_WEB_SERVER) );
      // Special case: replay not possible if WebServer is used with a SMIL file
      if (ps.GetIntValue("iServerType") == PublisherWizardData.SRV_WEB_SERVER)
      {
         boolean bHasSmilFile 
            = (!m_pwData.IsDenverDocument() && m_pwData.HasClips() && ps.GetBoolValue("bShowClipsOnSlides"));
         if (bHasSmilFile)
            bIsRunnableServerType = false;
      }

      if (bIsRunnableServerType)
      {
         NativeUtils.startFile(strHtml_);
      }
      else
      {
         // TODO: This message box has to be specified in the user concept ...
         JOptionPane.showMessageDialog(this, 
                                       NOT_RUNNABLE, ERROR, 
                                       JOptionPane.ERROR_MESSAGE);
      }

      //doQuit();
      // Bugfix #4085:
      return;
   }
   
   public void displayCurrentFile(String file, int fileSize,
                                  String action)
   {
      fileSize_ = fileSize;
      
      // Analyze "file" to be able to set the correct step for the ProgressManager
      // in case of OBJ and EVQ
      if (file.toLowerCase().endsWith(".obj")) {
          // Hack: Inform the progress bar thread, that the prevoius step (A/V conversion) 
          // is finished (otherwise it is still possible that a video progress call is sent 
          // from time to time ...) 
          m_pbt.setAudioVideoFinished();
          // "Step 1/3"
          m_progressManager.SetProgressStep("readObj");
          m_progressLabel.setText(READING_DATA);
          m_progressManager.SetProgressLabel(m_progressLabel.getText());
          m_progressManager.SetCurrentSubProgress(0.0f);
      }
      if (file.toLowerCase().endsWith(".evq")) {
          // "Step 2/3"
          m_progressManager.SetProgressStep("readEvq");
          m_progressLabel.setText(READING_DATA);
          m_progressManager.SetProgressLabel(m_progressLabel.getText());
          m_progressManager.SetCurrentSubProgress(0.0f);
      }

      if (file.equals("msg"))
      {
          m_progressLabel.setText(action);

          // Analyze action for "creating images"
          if (action.equalsIgnoreCase(CREATING_IMAGES)) {
              // "Step 3/3"
              m_progressManager.SetProgressStep("convertImages");
              m_progressManager.SetProgressLabel(m_progressLabel.getText());
              m_progressManager.SetCurrentSubProgress(0.0f);
          }
          // Analyze action for "creating HTML files"
          if (action.equalsIgnoreCase(CREATING_HTML)) {
              m_progressManager.SetProgressStep("createHtmlFiles");
              m_progressManager.SetProgressLabel(m_progressLabel.getText());
              m_progressManager.SetCurrentSubProgress(0.0f);
          }
          // Analyze action for "creating SCORM"
          if (action.equalsIgnoreCase(CREATING_SCORM)) {
              m_progressManager.SetProgressStep("createScorm");
              m_progressManager.SetProgressLabel(m_progressLabel.getText());
              m_progressManager.SetCurrentSubProgress(0.0f);
          }
          // Analyze action for "transferring files" (Upload or network drive)
          if (action.equalsIgnoreCase(TRANSFERRING_FILES)) {
              m_progressManager.SetProgressStep("transferFiles");
              m_progressManager.SetProgressLabel(m_progressLabel.getText());
              m_progressManager.SetCurrentSubProgress(0.0f);
          }
          // Analyze action for "transferring to Streaming Server"
          if (action.equalsIgnoreCase(TRANSFERRING_STREAMING_SERVER)) {
              m_progressManager.SetProgressStep("transferToStreamingServer");
              m_progressManager.SetProgressLabel(m_progressLabel.getText());
              m_progressManager.SetCurrentSubProgress(0.0f);
          }
      }
   }

   public void displayCurrentFileProgress(int bytesCount)
   {
      float fProgress = 1.0f * bytesCount / fileSize_;
      m_progressManager.SetCurrentSubProgress(fProgress);
      m_progressBar.setValue(m_progressManager.GetTotalProgressPercentage());
   }

   public void displayCurrentProgress(float fProgress)
   {
       // do nothing
   }

   public void displayCurrentProgressStep(String strStepId, String strLabel)
   {
       // do nothing.
   }

   private void defineProgressBarSteps(boolean bDoUploadCopy, 
           boolean bDoNetworkDriveCopy, 
           boolean bUseStreamingServer, 
           boolean bScormEnabled, 
           boolean bHasClips, 
           int iClipCount) {
       m_progressManager.Clear();
       // Step 1: Check Upload Server (Server/Upload only) 
       if (bDoUploadCopy)
           m_progressManager.AddProgressStep("checkUpload", 1);
       // Step 2: Convert audio/video
       m_progressManager.AddProgressStep("convertAudioVideo", 10);
       // Step 3: Convert clips (if any)
       if (bHasClips) {
           for (int i = 0; i < iClipCount; ++i) {
               String strClipId = "convertClip" + i;
               m_progressManager.AddProgressStep(strClipId, 10);
           }
       }
       // Step 4: Read OBJ
       m_progressManager.AddProgressStep("readObj", 3);
       // Step 5: Read EVQ
       m_progressManager.AddProgressStep("readEvq", 3);
       // Step 6: Convert images
       m_progressManager.AddProgressStep("convertImages", 10);
       // Step 7: Create HTML files
       m_progressManager.AddProgressStep("createHtmlFiles", 1);
       // Step 8: SCORM (if any)
       if (bScormEnabled)
           m_progressManager.AddProgressStep("createScorm", 10);
       // Step 9a: DoUploadCopy (Server/Upload only)
       if (bDoUploadCopy)
           m_progressManager.AddProgressStep("transferFiles", 5);
       // Step 9b: DoNetworkDriveCopy (Server/NetworkDrive only)
       if (bDoNetworkDriveCopy)
           m_progressManager.AddProgressStep("transferFiles", 3);
       // Step 10: Copy to Streaming Server (optional)
       if (bUseStreamingServer)
           m_progressManager.AddProgressStep("transferToStreamingServer", 5);
   }

   public void logMessage(String message)
   {
      // do nothing.
   }

   private class ProgressBarThread extends Thread 
   {
      private boolean m_bStopRequest = false;
      private boolean m_bAudioVideoFinished = false;
      private RealMediaConverter rmc_ = null;

      public ProgressBarThread(RealMediaConverter rmc)
      {
         super();
         rmc_ = rmc;
      }

      public void run()
      {
         DocumentData dd = m_pwData.GetDocumentData();
         ProfiledSettings ps = m_pwData.GetProfiledSettings();
         
         boolean bSeparateClips = ps.GetBoolValue("bShowClipsOnSlides");
         boolean bClipsOnly = dd.HasClips() && bSeparateClips 
                              && !dd.HasNormalVideo() && !dd.isClipStructured(); 
         boolean bIsClipConversion = false;
         
         long startMillisAud = System.currentTimeMillis();
         long startMillisVid = System.currentTimeMillis();
         float lastAudProgress = 0.0f;
         float lastVidProgress = 0.0f;
         
         clipStep_= 0;

         // If we have clips without video, we have to change the video progress label
         if (bClipsOnly)
         {
            VIDCLIPS = " Clip " + clipStep_ + "  ";
            clipStep_++;
            m_progressLabel.setText(CONVERT + VIDCLIPS + REMAINING + " (?) min.)");
         } else {
            m_progressLabel.setText(CONVERT_AV + " (?) min.)");
         }
         
         m_progressManager.SetProgressStep("convertAudioVideo");
         m_progressManager.SetProgressLabel(m_progressLabel.getText());
         m_progressManager.SetCurrentSubProgress(0.0f);
         
         while (!m_bStopRequest && !m_bAudioVideoFinished)
         {
            float audProgress = rmc_.getAudioProgress();
            float vidProgress = rmc_.getVideoProgress();

            // If we have clips, we have discontinuity in the video progress
            // ==> we have to reset startMillis and change the video label
            if (vidProgress < lastVidProgress)
            {
               bIsClipConversion = true;
               startMillisVid = System.currentTimeMillis();
               VIDCLIPS = " Clip " + clipStep_ + "  ";
               m_progressLabel.setText(CONVERT + VIDCLIPS + REMAINING + " (?) min.)");
               m_progressManager.SetProgressStep("convertClip" + clipStep_);
               m_progressManager.SetProgressLabel(m_progressLabel.getText());
               m_progressManager.SetCurrentSubProgress(0.0f);

               lastVidProgress = vidProgress; 
               clipStep_++;
            }

            // Update audio/video progress
            if ( dd.HasNormalVideo() || (dd.HasClips() && bSeparateClips) )
               m_progressManager.SetCurrentSubProgress(vidProgress);
            else
               m_progressManager.SetCurrentSubProgress(audProgress);
            m_progressBar.setValue(m_progressManager.GetTotalProgressPercentage());
            
            
            // reset startMillisAud, if audProgress is zero
            if (audProgress == 0.0)
               startMillisAud = System.currentTimeMillis();
            
            // Calculate remaining time for audio
            if (audProgress > 0.0 && audProgress != lastAudProgress)
            {
               long currentMillisAud = System.currentTimeMillis();
               long usedMillisAud = currentMillisAud - startMillisAud;
               long estMillisAud = (long)(usedMillisAud / audProgress);
               long remMillisAud = estMillisAud - usedMillisAud;
               
               int minutes = (int)(remMillisAud / (1000 * 60));
               int seconds = (int)((remMillisAud / 1000) % 60);
               
               String timeText = 
                  minutes+":"+((seconds < 10) ? "0" : "")+seconds+" min.)";
               
               if (!m_bStopRequest && !m_bAudioVideoFinished)
               {
                  if (!dd.HasNormalVideo()) {
                     m_progressLabel.setText(CONVERT_AV + " " + timeText);
                     m_progressManager.SetProgressLabel(m_progressLabel.getText());
                  }
               }
               
               lastAudProgress = audProgress; 
            }
            
            // reset startMillisVid, if vidProgress is zero
            if (vidProgress == 0.0)
               startMillisVid = System.currentTimeMillis();
            
            // Calculate remaining time for video
            // will never be executed without video
            if (vidProgress > 0.0 && vidProgress != lastVidProgress)
            {
               long currentMillisVid = System.currentTimeMillis();
               long usedMillisVid = currentMillisVid - startMillisVid;
               long estMillisVid = (long)(usedMillisVid / vidProgress);
               long remMillisVid = estMillisVid - usedMillisVid;
               
               int minutes = (int)(remMillisVid / (1000 * 60));
               int seconds = (int)((remMillisVid / 1000) % 60);
               
               String timeText = 
                  minutes+":"+((seconds < 10) ? "0" : "")+seconds+" min.)";
               
               if (!m_bStopRequest && !m_bAudioVideoFinished)
               {
                  if (bIsClipConversion) 
                     m_progressLabel.setText(CONVERT + VIDCLIPS + REMAINING + " " + timeText);
                  else
                     m_progressLabel.setText(CONVERT_AV + " " + timeText);
                  m_progressManager.SetProgressLabel(m_progressLabel.getText());
               }
               
               lastVidProgress = vidProgress; 
            }
            
            try
            {
               sleep(500);
            }
            catch (InterruptedException e)
            {
               // ignore
            }
         }

      }

      public void requestStop() {
          m_bStopRequest = true;
      }
      
      public void setAudioVideoFinished() {
          m_bAudioVideoFinished = true;
      }
   }

   private class CancelButtonListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         requestCancel();
      }
   }
}
