docker run --rm --name dashing -it -p 3030:3030 \
	-v $PWD/dashboards:/dashing/dashboards \
	-v $PWD/widgets:/dashing/widgets \
	larrycai/dashing dashing start