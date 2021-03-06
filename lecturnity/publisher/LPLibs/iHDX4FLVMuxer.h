//----------------------------------------------------------------
// HDX4 Flash video muxer.
//----------------------------------------------------------------
#ifndef iHDX4FLVMuxerH 
#define	iHDX4FLVMuxerH


// {03112886-43E1-4c80-9F46-6AD10AAAC383}
DEFINE_GUID(CLSID_HDX4FLVMuxer, 
			0x03112886, 0x43e1, 0x4c80, 0x9f, 0x46, 0x6a, 0xd1, 0xa, 0xaa, 0xc3, 0x83);
// {03112886-43E1-4c80-9F46-6AD10AAAC384}
DEFINE_GUID(IID_IHDX4HDX4FLVMuxer, 
			0x03112886, 0x43e1, 0x4c80, 0x9f, 0x46, 0x6a, 0xd1, 0xa, 0xaa, 0xc3, 0x84);




#ifndef FLASH_VIDEO_NAMES
#define FLASH_VIDEO_NAMES
	const DWORD	AVC_FOURCC_flv1	= MAKEFOURCC('f','l','v','1'); // 0x31766C66
	const DWORD	AVC_FOURCC_FLV1	= MAKEFOURCC('F','L','V','1'); // 0x31564C46
	DEFINE_GUID(MEDIASUBTYPE_flv1, AVC_FOURCC_flv1, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);
	DEFINE_GUID(MEDIASUBTYPE_FLV1, AVC_FOURCC_FLV1, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);

	//-- 
	const DWORD	AVC_FOURCC_VP60	= MAKEFOURCC('V','P','6','0'); 
	const DWORD	AVC_FOURCC_vp60	= MAKEFOURCC('v','p','6','0'); 
	DEFINE_GUID(MEDIASUBTYPE_VP60, AVC_FOURCC_VP60, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);
	DEFINE_GUID(MEDIASUBTYPE_vp60, AVC_FOURCC_vp60, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);

	const DWORD	AVC_FOURCC_VP61	= MAKEFOURCC('V','P','6','1'); 
	const DWORD	AVC_FOURCC_vp61	= MAKEFOURCC('v','p','6','1'); 
	DEFINE_GUID(MEDIASUBTYPE_VP61, AVC_FOURCC_VP61, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);
	DEFINE_GUID(MEDIASUBTYPE_vp61, AVC_FOURCC_vp61, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);

	const DWORD	AVC_FOURCC_VP62	= MAKEFOURCC('V','P','6','2'); 
	const DWORD	AVC_FOURCC_vp62	= MAKEFOURCC('v','p','6','2'); 
	DEFINE_GUID(MEDIASUBTYPE_VP62, AVC_FOURCC_VP62, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);
	DEFINE_GUID(MEDIASUBTYPE_vp62, AVC_FOURCC_vp62, 0x0000, 0x0010, 0x80, 0x00, 0x00, 0xaa, 0x00, 0x38, 0x9b,0x71);
#endif

//----------------------------------------------------------------
#ifdef __cplusplus
extern "C" {
#endif
	DECLARE_INTERFACE_(IHDX4FLVMuxer, IUnknown)
	{
		STDMETHOD(SetXMLSettings) (THIS_ const BSTR bstrXMLSettings) PURE;
		STDMETHOD(GetXMLSettings) (THIS_ BSTR* pbstrXMLConfigString, THIS_ const BSTR bstrDocumentType) PURE;
	};
#ifdef __cplusplus
}
#endif

//----------------------------------------------------------------
#endif /* iHDX4FLVMuxerH */