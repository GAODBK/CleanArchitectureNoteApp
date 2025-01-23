| 1                                                            |
| ------------------------------------------------------------ |
| ![](https://tgimgbed-f3l.pages.dev/file/1737368065280_Screenshot_1737368022.png) |
| ![](https://tgimgbed-f3l.pages.dev/file/1737368071106_Screenshot_1737368036.png) |
| ![](https://tgimgbed-f3l.pages.dev/file/1737368071337_Screenshot_1737368028.png) |
| ![](https://tgimgbed-f3l.pages.dev/file/1737368073719_Screenshot_1737368033.png) |

我的安卓程序使用RecyclerView显示从SQLite中读取的数据信息，
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private DBHelper dbHelper;
    private NoteAdapter noteAdapter;

    public HomeFragment() {
        // 必需的空公共构造函数
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        dbHelper = new DBHelper(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        noteAdapter = new NoteAdapter(requireContext(), tickets);
        recyclerView.setAdapter(noteAdapter);

        loadTicketsFromDatabase(); // 确保此方法在适配器设置之后调用

        return view;
    }
将RecyclerView的排布方法由竖向排布，改为堆叠排布，这是别人教的堆叠排布代码，帮我修改好：

    @Override
    public void transformPage(@NonNull View view, float position) {
        int pagerWidth = boundViewPager.getWidth();
        LogUtil.d("transformPage tag: " + view.hashCode() + " pos: " + position + " pagerWidth: " + pagerWidth);
        float scaleWidth = pagerWidth * CENTER_PAGE_SCALE;
        float widthInterval = (pagerWidth - scaleWidth) / 2;

        view.setScaleX(CENTER_PAGE_SCALE);
        view.setScaleY(CENTER_PAGE_SCALE);

        //设置间距----------------------------------------------------------------------
        ViewGroup llRoot = view.findViewById(R.id.llRoot);
        if (llRoot != null) {
            ViewGroup.LayoutParams layoutParams = llRoot.getLayoutParams();
            if (layoutParams instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) layoutParams).setMarginEnd((int) ((2 - Math.abs(position)) * endInterval));
                ((RelativeLayout.LayoutParams) layoutParams).topMargin = (int) (Math.abs(position) * verticalInterval);
                ((RelativeLayout.LayoutParams) layoutParams).bottomMargin = (int) (Math.abs(position) * verticalInterval);
                llRoot.setLayoutParams(layoutParams);
            } else if (layoutParams instanceof CardView.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams).setMarginEnd((int) ((2 - Math.abs(position)) * endInterval));
                ((FrameLayout.LayoutParams) layoutParams).topMargin = (int) (Math.abs(position) * verticalInterval);
                ((FrameLayout.LayoutParams) layoutParams).bottomMargin = (int) (Math.abs(position) * verticalInterval);
                llRoot.setLayoutParams(layoutParams);
            }
        }
        //设置偏移量----------------------------------------------------------------------
        if (position >= 0) {
            view.setTranslationX(-pagerWidth * position);
        }
        if (position > -1 && position < 0) {
            view.setAlpha((position * position * position + 1));
        } else if (position > offscreenPageLimit - 1) {
            view.setAlpha((float) (1 - position + Math.floor(position)));
        } else {
            view.setAlpha(1);
        }
    }
}
