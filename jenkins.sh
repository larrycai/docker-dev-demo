docker run --rm -it --name jenkins -p 8080:8080 \
	--link dashing:dashing \
	-v $PWD/jenkins:/opt/jenkins/data/jobs/longjobs/workspace \
	 larrycai/jenkins
