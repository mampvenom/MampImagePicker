package kr.co.mamp.imagepicker.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import kr.co.mamp.imagepicker.Image;
import kr.co.mamp.imagepicker.MampImagePicker;
import kr.co.mamp.imagepicker.R;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {


    /**
     * 이미지 리스트.
     */
    private List<Image> images = new ArrayList<>();
//    /**
//     * 선택된 이미지 리스트.
//     */
//    private List<Image> checkedList = new ArrayList<>();
    /**
     * 카메라 클릭.
     */
    private View.OnClickListener onClickCamera;
    /**
     * 설정셋.
     */
    private MampImagePicker.Builder builder;


    public ImageAdapter(MampImagePicker.Builder builder) {
        this.builder = builder;

    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image_selector, parent, false);
        return new ImageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image image = images.get(position);
        if (position == 0 && builder != null && builder.fromCamera) {
            holder.imageView.setImageResource(R.drawable.ic_photo_camera_white_48dp);
            holder.imageView.setRotation(0);
            holder.checkBox.setChecked(false);
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.imageView.setImageURI(image.getThumbnail());
            holder.imageView.setRotation(image.getOrientation());
            holder.checkBox.setChecked(builder != null && builder.checkedImages.contains(image));
            holder.checkBox.setVisibility(builder == null ? View.INVISIBLE : View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return images.size();
    }


    /**
     * 이미지 리스트 세팅.
     */
    public void setImages(List<Image> images) {
        this.images = images;
        if (builder != null && builder.fromCamera) this.images.add(0, null);
        notifyDataSetChanged();
    }


    public List<Image> getImages() {
        return images;
    }


    /**
     * 선택된 이미지 리스트 가져오기.
     */
    public List<Image> getCheckedList() {
        return builder.checkedImages;
    }


    /**
     * 카메라 클릭 이벤트.
     */
    public void setOnClickCamera(View.OnClickListener onClickCamera) {
        this.onClickCamera = onClickCamera;
    }


    /**
     * 뷰 홀더.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        CheckBox checkBox;

        ImageViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
            checkBox.setClickable(false);
            if (builder != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position == 0 && builder.fromCamera) {
                            if (onClickCamera != null) onClickCamera.onClick(v);
                        } else {
                            Image image = images.get(position);
                            if (builder.checkedImages.contains(image))
                                builder.checkedImages.remove(image);
                            else builder.checkedImages.add(image);
                            notifyItemChanged(position);
                            if (builder.singlePickCallback != null && builder.checkedImages.size() > 1) {
                                int prevChecked = images.indexOf(builder.checkedImages.get(0));
                                builder.checkedImages.remove(0);
                                notifyItemChanged(prevChecked);
                            }
                        }
                    }
                });
                if (builder.longClickCallback != null) {
                    itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int position = getAdapterPosition();
                            if (position == 0 && builder.fromCamera) return false;
                            else {
                                builder.longClickCallback.onLongClick(images.get(getAdapterPosition()));

//                            if (v.getContext() instanceof FragmentActivity) {
//                                Image image = images.get(position);
//                                FragmentManager manager =
//                                        ((FragmentActivity) v.getContext())
//                                                .getSupportFragmentManager();
//                                ImageViewer.newInstance(image).show(manager);
//                            }
                                return true;
                            }
                        }
                    });
                }
            }
        }
    }
}
