#!/usr/bin/env filebot -script


// raw / slow mode (i.e. use libmediainfo and write xattr)
if (_args.mode == /raw/) {
	log.finest "# ${MediaInfo.version()}"

	args.files.each{ f ->
		try(MediaInfo mi = new MediaInfo()) {
			mi.option("Language", "raw")
			mi.option("Complete", "1")

			def read = mi.openViaBuffer(f)
			def raw = mi.inform()

			// print stats
			log.finest "# $f [${read.displaySize} of ${f.displaySize}]"

			// write xattr
			if (raw) {
				f.xattr['mediainfo'] = raw
				f.xattr['mediainfo.mtime'] = f.lastModified() as String	
			}
		}
	}
}


// default / fast mode (i.e. use local cache or xattr or libmediainfo)
args.files.each{ f ->
	log.fine "\n# $f"

	f.mediaInfo.each{ kind ->
		kind.each{ stream -> 
			log.finest "\n${stream.StreamKind} #${stream.StreamKindID}"

			// find optimal padding
			def pad = stream.keySet().flatten().collect{ it.length() }.max()
			stream.each{ k,v -> 
				log.info "${k.padRight(pad)} : $v"
			}
		}
	}
}
